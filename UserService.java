package com.dsoft.m2u.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dsoft.m2u.api.dto.DeviceDTO;
import com.dsoft.m2u.api.dto.ErrorCodeEnum;
import com.dsoft.m2u.api.dto.UserChangePasswordDTO;
import com.dsoft.m2u.api.dto.UserDTO;
import com.dsoft.m2u.api.dto.UserResDTO;
import com.dsoft.m2u.api.request.DeviceRequest;
import com.dsoft.m2u.api.request.UserRegisterRequest;
import com.dsoft.m2u.api.response.BaseResponse;
import com.dsoft.m2u.common.ScreenMessageConstants;
import com.dsoft.m2u.domain.Device;
import com.dsoft.m2u.domain.Role;
import com.dsoft.m2u.domain.RoleName;
import com.dsoft.m2u.domain.User;
import com.dsoft.m2u.domain.specification.UserSpecs;
import com.dsoft.m2u.exception.ResourceInternalServerError;
import com.dsoft.m2u.exception.ResourceInvalidInputException;
import com.dsoft.m2u.exception.ResourceNotFoundException;
import com.dsoft.m2u.push.notification.service.FCMInitializer;
import com.dsoft.m2u.repository.DeviceRepository;
import com.dsoft.m2u.repository.RoleRepository;
import com.dsoft.m2u.repository.UserRepository;
import com.dsoft.m2u.utils.MapperUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

/**
 * [Description]:<br>
 * [ Remarks ]:<br>
 * [Copyright]: Copyright (c) 2020<br>
 * 
 * @author D-Soft Joint Stock Company
 * @version 1.0
 */
@Service
public class UserService {

	private static final Logger logger = LogManager.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Autowired
	private PasswordEncoder encoder;
	
	@Autowired
	private FCMInitializer fcmService;
	
	@Autowired
	private DeviceRepository deviceRepository;

	public BaseResponse createUserAccount(UserDTO userDTO) {
		logger.info("UserServiceImpl.createUserAccount");
		User user = new User();
		if (userRepository.existsByEmail(userDTO.getEmail())) {
			throw new ResourceInvalidInputException("Email already exists");
		}
		if (userRepository.existsByUsername(userDTO.getUsername())) {
			throw new ResourceInvalidInputException("Username already exists");
		}
		if (userRepository.existsByPhone(userDTO.getPhone())) {
			throw new ResourceInvalidInputException("Phone number already exists");
		}
		if (userDTO.getUsername() == null || userDTO.getUsername().isEmpty()) {
			throw new ResourceInvalidInputException("Username", "not invalid");
		}
		user.setUsername(userDTO.getUsername());
		if (userDTO.getEmail() == null || userDTO.getEmail().isEmpty()) {
			throw new ResourceInvalidInputException("Email", "not invalid");
		}
		user.setEmail(userDTO.getEmail());
		if (userDTO.getPhone() == null || userDTO.getPhone().isEmpty()) {
			throw new ResourceInvalidInputException("Phone", "not invalid");
		}
		user.setPhone(userDTO.getPhone());
		if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
			throw new ResourceInvalidInputException("Password", "not invalid");
		}
		user.setPassword(encoder.encode(userDTO.getPassword()));
		Set<Role> roles = new HashSet<Role>();
		if (userDTO.getRole().equalsIgnoreCase("admin")) {
			roles.addAll(roleRepository.findByNameAndActive(RoleName.ADMIN, true));
		} else {
			roles.addAll(roleRepository.findByNameAndActive(RoleName.USER, true));
		}
		user.setRoles(roles);
		user.setCreatedAt(new Date());
		user.setUpdatedAt(new Date());
		user.setActive(true);
		userRepository.save(user);

		return new BaseResponse(ScreenMessageConstants.SUCCESS, "Create user successfully!");
	}

	public BaseResponse getAll(Integer pageNo, Integer pageSize, List<String> roles) {
		logger.info("UserServiceImpl.getAll");	
		Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by("createdAt").descending());
		Specification<User> spec = Specification.where(UserSpecs.getUserByActiveSpec(true));
		if (roles != null && roles.size() != 0) {
			List<RoleName> rolenames = RoleName.fromValue(roles);  
			spec = spec.and(UserSpecs.getUserByRoleNameSpec(rolenames));
		}
		
		Page<User> pagedResult = userRepository.findAll(spec, pageable);
		Integer totalItem = (int) userRepository.count(spec);
		List<UserResDTO> userResponses = new ArrayList<>();
		if (pagedResult.hasContent()) {
			for (User user : pagedResult.getContent()) {
				UserResDTO userResponse = MapperUtil.mapper(user, UserResDTO.class);
				userResponses.add(userResponse);
			}
		}
		return new BaseResponse(ScreenMessageConstants.SUCCESS, userResponses, totalItem);
	}

	public BaseResponse changePassword(UserChangePasswordDTO dto) {
		logger.info("UserServiceImpl.changePassword");
		User user = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new ResourceNotFoundException(ErrorCodeEnum.USER_FIREBASE_NOT_FOUND, "User", "id", dto.getUserId()));
		if (!encoder.matches(dto.getOldPassword(), user.getPassword())) {
			throw new ResourceInvalidInputException("Old password does not correct!");
		}
		if (encoder.matches(dto.getNewPassword(), user.getPassword())) {
			throw new ResourceInvalidInputException("New password is not allowed match with the current password!");
		}
		if (dto.getNewPassword().trim().length() < 8 || dto.getNewPassword().trim().length() > 100) {
			throw new ResourceInvalidInputException(
					"Password must be longer than 8 characters and shorter than 100 characters ");
		}
		if (!dto.getNewPassword().trim().equalsIgnoreCase(dto.getConfirmNewPassword().trim())) {
			throw new ResourceInvalidInputException("New password must be match with the confirm new password!");
		}

		user.setPassword(encoder.encode(dto.getNewPassword()));
		userRepository.save(user);
		return new BaseResponse(ScreenMessageConstants.SUCCESS, "Change password successfully!");
	}

	public BaseResponse deactivate(String id) {
		logger.info("UserServiceImpl.deactivate");
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorCodeEnum.USER_FIREBASE_NOT_FOUND, "User", "id", id));
		if (user == null) {
			throw new ResourceNotFoundException(ErrorCodeEnum.USER_FIREBASE_NOT_FOUND, "User", "id", id);
		}
		user.setActive(false);

		userRepository.save(user);
		return new BaseResponse(ScreenMessageConstants.SUCCESS, "Deactive user successfully");
	}

	public BaseResponse delete(String id) {
		logger.info("UserServiceImpl.delete");
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorCodeEnum.USER_FIREBASE_NOT_FOUND, "User", "id", id));
		if (user == null) {
			throw new ResourceNotFoundException(ErrorCodeEnum.USER_FIREBASE_NOT_FOUND, "User", "id", id);
		}
		user.setActive(false);

		userRepository.delete(user);
		return new BaseResponse(ScreenMessageConstants.SUCCESS, "Delete user successfully");
	}

	public BaseResponse getById(String id) {
		logger.info("UserServiceImpl.getById");
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(ErrorCodeEnum.USER_FIREBASE_NOT_FOUND, "User", "id", id));
		if (user == null) {
			throw new ResourceNotFoundException(ErrorCodeEnum.USER_FIREBASE_NOT_FOUND, "User", "id", id);
		}
		return new BaseResponse(ScreenMessageConstants.SUCCESS, MapperUtil.mapper(user, UserResDTO.class));
	}
	
	public BaseResponse getByFireBaseId(String fireBaseId) {
		logger.info("UserServiceImpl.getByFireBaseId");
		User user = userRepository.findByFireBaseId(fireBaseId).orElseThrow(() -> new ResourceNotFoundException(ErrorCodeEnum.USER_FIREBASE_NOT_FOUND, "User", "fireBaseId", fireBaseId));
		if (user == null) {
			throw new ResourceNotFoundException(ErrorCodeEnum.USER_FIREBASE_NOT_FOUND, "User", "fireBaseId", fireBaseId);
		}
		return new BaseResponse(ScreenMessageConstants.SUCCESS, MapperUtil.mapper(user, UserResDTO.class));
	}

	public BaseResponse updateUserAccount(UserDTO userUpdateDTO, String id) {
		logger.info("UserServiceImpl.updateUserAccount");
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

		if (userRepository.existsByEmailAndIdNot(userUpdateDTO.getEmail(), id)) {
			throw new ResourceInvalidInputException("Email already exists");
		}
		if (userRepository.existsByUsernameAndIdNot(userUpdateDTO.getUsername(), id)) {
			throw new ResourceInvalidInputException("Username already exists");
		}
		if (userRepository.existsByPhoneAndIdNot(userUpdateDTO.getPhone(), id)) {
			throw new ResourceInvalidInputException("Phone number already exists");
		}

		if (userUpdateDTO.getUsername() == null || userUpdateDTO.getUsername().isEmpty()) {
			throw new ResourceInvalidInputException("Name", "not invalid");
		}
		user.setUsername(userUpdateDTO.getUsername());
		if (userUpdateDTO.getEmail() == null || userUpdateDTO.getEmail().isEmpty()) {
			throw new ResourceInvalidInputException("Email", "not invalid");
		}
		user.setEmail(userUpdateDTO.getEmail());
		if (userUpdateDTO.getPhone() == null || userUpdateDTO.getPhone().isEmpty()) {
			throw new ResourceInvalidInputException("Phone", "not invalid");
		}
		user.setPhone(userUpdateDTO.getPhone());
		if (userUpdateDTO.getPassword() == null || userUpdateDTO.getPassword().isEmpty()) {
			throw new ResourceInvalidInputException("Password", "not invalid");
		}
		user.setPassword(encoder.encode(userUpdateDTO.getPassword()));

		Set<Role> roles = new HashSet<Role>();
		if (userUpdateDTO.getRole().equalsIgnoreCase("admin")) {
			roles.addAll(roleRepository.findByNameAndActive(RoleName.ADMIN, true));			
		} else {
			roles.addAll(roleRepository.findByNameAndActive(RoleName.USER, true));
		}
		user.setRoles(roles);

		user.setCreatedAt(new Date());
		user.setUpdatedAt(new Date());
		user.setActive(true);
		userRepository.save(user);
		return new BaseResponse(ScreenMessageConstants.SUCCESS, "Update user successfully");
	}
	
	public BaseResponse registerUserAccount(UserRegisterRequest request) {
		logger.info("UserServiceImpl.registerUserAccount");		

		// Verify with firebase token LATER
//		try {
//			FirebaseToken decodedToken = FirebaseAuth.getInstance(fcmService.firebaseApp).verifyIdToken(request.getFireBaseId());
//			if (decodedToken == null || decodedToken.getUid() == null)
//				throw new ResourceInvalidInputException("Firebase Token", "not invalid");
//		} catch (FirebaseAuthException e) {
//			throw new ResourceInternalServerError("Firebase Error" + e.getMessage());
//		}
		User user = this.userRepository.findByFireBaseId(request.getFireBaseId()).orElse(null);
		if(user == null) {
			user = new User();
			user.setFireBaseId(request.getFireBaseId());
			user.setCreatedAt(new Date());
			user.setUpdatedAt(new Date());
			user.setActive(true);
			// Create new user
			userRepository.save(user);
		}
		//Create device
		this.insertDevice(new DeviceRequest(request.getDeviceId(), request.getDeviceType(), request.getFireBaseId()));
		
		//return
		UserDTO userDTO = MapperUtil.mapper(user, UserDTO.class);				
		return new BaseResponse(ScreenMessageConstants.SUCCESS, userDTO);
	}
	
	public BaseResponse insertDevice(DeviceRequest request) {
		logger.info("UserServiceImpl.insertDevice " +request.toString());
		
		// Verify with firebase LATER
							
		User userOpt = this.userRepository.findByFireBaseId(request.getFireBaseId()).orElseThrow(() -> new ResourceNotFoundException(ErrorCodeEnum.USER_FIREBASE_NOT_FOUND, "User", "fireBaseId", request.getFireBaseId()));

		Device device = this.deviceRepository.findByIdAndActiveAndUserFireBaseId(request.getDeviceId(), true, userOpt.getFireBaseId());
		
		if(device == null) {
			device = new Device();
			device.setId(request.getDeviceId());
			device.setDeviceType(request.getDeviceType());
			device.setUser(userOpt);
			device.setActive(true);
			device.setCreatedAt(new Date());
			device.setUpdatedAt(new Date());
		}else {
			device.setUpdatedAt(new Date());
		}
		
		// Create new deviceID for user
		deviceRepository.save(device);
		DeviceDTO deviceDTO = MapperUtil.mapper(device, DeviceDTO.class);

		return new BaseResponse(ScreenMessageConstants.SUCCESS, deviceDTO);
	}
}