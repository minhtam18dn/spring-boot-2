package com.dsoft.m2u.utils;

import org.modelmapper.ModelMapper;
/**
 * [Description]:<br>
 * [ Remarks ]:<br>
 * [Copyright]: Copyright (c) 2020<br>
 * 
 * @author D-Soft Joint Stock Company
 * @version 1.0
 */
public class MapperUtil {
    private static final ModelMapper modelMapper = new ModelMapper();

    /**
     * <p> Map source data to destination </p>
     *
     * @param src
     * @param destination
     * @return destination
     */
    public static <D, S> D mapper(S src, Class<D> destination) {
        return modelMapper.map(src, destination);
    }
}
