package com.dsoft.m2u.service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.dsoft.m2u.api.dto.PriceBySlotResCustomDTO;
import com.dsoft.m2u.api.dto.PriceBySlotResDTO;
import com.dsoft.m2u.api.dto.SlotTypeDTO;
import com.dsoft.m2u.api.request.PriceBySlotRequest;
import com.dsoft.m2u.api.response.BaseResponse;
import com.dsoft.m2u.common.CommonFunctions;
import com.dsoft.m2u.common.ScreenMessageConstants;
import com.dsoft.m2u.domain.Facility;
import com.dsoft.m2u.domain.FacilityTranslate;
import com.dsoft.m2u.domain.LanguageCode;
import com.dsoft.m2u.domain.PriceBySlot;
import com.dsoft.m2u.domain.SlotType;
import com.dsoft.m2u.domain.specification.PriceBySlotSpec;
import com.dsoft.m2u.exception.ResourceInvalidInputException;
import com.dsoft.m2u.exception.ResourceNotFoundException;
import com.dsoft.m2u.repository.FacilityRepository;
import com.dsoft.m2u.repository.PriceBySlotRepository;
import com.dsoft.m2u.repository.SlotTypeRepository;
import com.dsoft.m2u.utils.MapperUtil;

/**
 * [Description]:<br>
 * [ Remarks ]:<br>
 * [Copyright]: Copyright (c) 2020<br>
 * 
 * @author D-Soft Joint Stock Company
 * @version 1.0
 */
@Service
public class PriceBySlotService {
	private static final Logger logger = LogManager.getLogger(PriceBySlotService.class);
	
	@Autowired
	private PriceBySlotRepository priceBySlotRepository;
	
	@Autowired
	private FacilityRepository facilityRepository;
	
	@Autowired
	private SlotTypeRepository slotTypeRepository;
	
	public BaseResponse getAll() {
		logger.info("PriceBySlotService.getAll");
		Specification<PriceBySlot> spec = Specification.where(PriceBySlotSpec.getPriceBySlotByActiveSpec(true));
		List<PriceBySlotResCustomDTO> listPriceBySlot = new ArrayList<>();
		List<PriceBySlot> priceBySlotList = priceBySlotRepository.findAll(spec); 

		int facilityIndex = 0;
		int conditionIndex = 0;
		for(PriceBySlot priceBySlot : priceBySlotList) {
			PriceBySlotResCustomDTO priceResDto = MapperUtil.mapper(priceBySlot, PriceBySlotResCustomDTO.class);
			priceResDto.setPrice(CommonFunctions.formatDoubleToString(priceBySlot.getPrice()));
			priceResDto.setConditionPrice(priceBySlot.getConditionPrice().getId());
			for (FacilityTranslate facilityTranslate : priceBySlot.getFacility().getTranslates()) {
				if (facilityTranslate.getCode() == LanguageCode.VI) {
					priceResDto.setFacilityTitleVi(facilityTranslate.getTitle());
				}
				if (facilityTranslate.getCode() == LanguageCode.EN) {
					priceResDto.setFacilityTitleEn(facilityTranslate.getTitle());				
				}
			}
			if(priceBySlotList.indexOf(priceBySlot) == 0) {
				priceResDto.setFaciIndex(facilityIndex);
				priceResDto.setCondiIndex(conditionIndex);
			} else {
				PriceBySlot priceBySlotPrev = priceBySlotList.get(priceBySlotList.indexOf(priceBySlot) - 1);
				if(priceBySlot.getFacility().getId().equals(priceBySlotPrev.getFacility().getId())) {
					priceResDto.setFaciIndex(++ facilityIndex);
					if(priceBySlot.getConditionPrice() == priceBySlotPrev.getConditionPrice()) {
						priceResDto.setCondiIndex(++ conditionIndex);
					} else {
						conditionIndex = 0;
						priceResDto.setCondiIndex(conditionIndex);
					}
				} else {
					facilityIndex = 0;
					conditionIndex = 0;
					priceResDto.setFaciIndex(facilityIndex);
					priceResDto.setCondiIndex(conditionIndex);
				}
			}
			listPriceBySlot.add(priceResDto);
		}
		return new BaseResponse(ScreenMessageConstants.SUCCESS, listPriceBySlot, listPriceBySlot.size());
	}
		
	public BaseResponse getById(String id) {
		logger.info("PriceBySlotService.getById");
		PriceBySlot priceBySlot = priceBySlotRepository.findByIdAndActive(Integer.parseInt(id), true).orElseThrow(
			() -> new ResourceNotFoundException("PriceBySlot", "id", id));
		PriceBySlotResDTO priceResDto = MapperUtil.mapper(priceBySlot, PriceBySlotResDTO.class);
		priceResDto.setConditionPrice(priceBySlot.getConditionPrice().getId());
		priceResDto.setPrice(CommonFunctions.formatDoubleToString(priceBySlot.getPrice()));
		
		return new BaseResponse(ScreenMessageConstants.SUCCESS, priceResDto, 1);
	}
	
	public BaseResponse insert(@Valid PriceBySlotRequest request) {
		logger.info("PriceBySlotService.insert");
		Facility facility = facilityRepository.findByIdAndActive(request.getFacilityId(), true).orElseThrow(
				() -> new ResourceNotFoundException("Facility","id", request.getFacilityId()));
		List<PriceBySlot> priceBySlots = priceBySlotRepository.findByFacilityIdAndActive(facility.getId(), true);
		checkValidateTime(request.getStartTime(), request.getEndTime(), priceBySlots, request.getCondition());
		PriceBySlot priceBySlot = new PriceBySlot();
		priceBySlot.setFacility(facility);
		priceBySlot.setStartTime(LocalTime.parse(request.getStartTime()));
		priceBySlot.setEndTime(LocalTime.parse(request.getEndTime()));
		priceBySlot.setPrice(Double.parseDouble(request.getPrice()));
		SlotType slotType = slotTypeRepository.findById(request.getCondition()).get();
		priceBySlot.setConditionPrice(slotType);
		priceBySlot.setActive(true);
		priceBySlot.setCreatedAt(new Date());
		priceBySlot.setUpdatedAt(new Date());
		priceBySlotRepository.save(priceBySlot);
		return new BaseResponse(ScreenMessageConstants.SUCCESS, "Insert price by slot sucessfully!", 1);
	}
	
	public BaseResponse update(PriceBySlotRequest request, String id) {
		logger.info("PriceBySlotService.update");
		PriceBySlot priceBySlot = priceBySlotRepository.findByIdAndActive(Integer.parseInt(id), true).orElseThrow(
				() -> new ResourceNotFoundException("PriceBySlot", "id", id));;
		if(request.getFacilityId() != null && !request.getFacilityId().isEmpty()) {
			if(!priceBySlot.getFacility().getId().equals(request.getFacilityId())) {
				Facility facility = facilityRepository.findByIdAndActive(request.getFacilityId(), true).orElseThrow(
						() -> new ResourceNotFoundException("Facility","id", request.getFacilityId()));
				priceBySlot.setFacility(facility);
			}
		}		
		List<PriceBySlot> priceBySlots = priceBySlotRepository.findByFacilityIdAndActive(priceBySlot.getFacility().getId(), true);;
		if(request.getCondition() != null && !request.getCondition().isEmpty()) {
			priceBySlot.setConditionPrice(slotTypeRepository.findById(request.getCondition()).get());
		}
		if(priceBySlots.remove(priceBySlot)) {
			priceBySlots.remove(priceBySlot);
			checkValidateTime(request.getStartTime(), request.getEndTime(), priceBySlots, priceBySlot.getConditionPrice().toString());
		}
		if(request.getStartTime() != null && !request.getStartTime().isEmpty()) {
			priceBySlot.setStartTime(LocalTime.parse(request.getStartTime()));
		}
		if(request.getEndTime() != null && !request.getEndTime().isEmpty()) {
			priceBySlot.setEndTime(LocalTime.parse(request.getEndTime()));
		}
		if(request.getPrice() != null && !request.getPrice().isEmpty()) {
			priceBySlot.setPrice(Double.parseDouble(request.getPrice()));
		}
		
		priceBySlot.setUpdatedAt(new Date());
		priceBySlotRepository.save(priceBySlot);
		return new BaseResponse(ScreenMessageConstants.SUCCESS, "Update price by slot sucessfully!", 1);
	}
	
	public BaseResponse delete(String id) {
		logger.info("PriceBySlotService.delete");
		PriceBySlot priceBySlot = priceBySlotRepository.findByIdAndActive(Integer.parseInt(id), true).orElseThrow(
			() -> new ResourceNotFoundException("PriceBySlot", "id", id));
		priceBySlot.setActive(false);
		priceBySlot.setUpdatedAt(new Date());
		priceBySlotRepository.save(priceBySlot);
		
		return new BaseResponse(ScreenMessageConstants.SUCCESS, "Delete price by slot successfully");
	}
	
	private void checkValidateTime(String startTime, String endTime, List<PriceBySlot> priceBySlots, String condition) {
		LocalTime start = LocalTime.parse(startTime);
		LocalTime end = LocalTime.parse(endTime);

		for(PriceBySlot priceBySlot : priceBySlots) {
			if((!start.isBefore(end))) {
				throw new ResourceInvalidInputException("Start time must be before end time!");
			}
			if(condition.equals(priceBySlot.getConditionPrice().getId()) && start.isAfter(priceBySlot.getStartTime()) && start.isBefore(priceBySlot.getEndTime())){
				throw new ResourceInvalidInputException("Start time alredy used!");
			}
			if(condition.equals(priceBySlot.getConditionPrice().getId())&& end.isAfter(priceBySlot.getStartTime()) && end.isBefore(priceBySlot.getEndTime())) {
				throw new ResourceInvalidInputException("End time alredy used!");
			}
			if(condition.equals(priceBySlot.getConditionPrice().getId()) && !start.isAfter(priceBySlot.getStartTime()) && !end.isBefore(priceBySlot.getEndTime())) {
				throw new ResourceInvalidInputException("Start time and end time alredy used!");
			}
		}
	}
	
	public BaseResponse getAllConditionPrice() {
		List<SlotType> slotTypes = this.slotTypeRepository.findAll();
		List<SlotTypeDTO> slotTypeDTOs = new ArrayList<SlotTypeDTO>();
		for (SlotType slotType : slotTypes) {
			slotTypeDTOs.add(MapperUtil.mapper(slotType, SlotTypeDTO.class));
		}
		return new BaseResponse(ScreenMessageConstants.SUCCESS, slotTypeDTOs);
	}
}