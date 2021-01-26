package com.dsoft.m2u.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.gif.GifControlDirectory;
import com.dsoft.m2u.M2uApplication;

/**
 * [Description]:<br>
 * [ Remarks ]:<br>
 * [Copyright]: Copyright (c) 2020<br>
 * 
 * @author D-Soft Joint Stock Company
 * @version 1.0
 */

public class FileUtil {

	private static final Logger logger = LogManager.getLogger(M2uApplication.class);

	private FileUtil() {
		super();
	}

	public static String toString(File file) {
		try {
			return new String(Files.readAllBytes(file.toPath()));
		} catch (IOException e) {
			logger.warn("Cannot read file:: {}", file.getPath(), e);
			return null;
		}
	}

	public static InputStream getInputStream(String path) {
		try {
			return FileUtil.class.getClassLoader().getResourceAsStream(path);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static int getDuration(File file) throws ImageProcessingException, IOException {
		Metadata metadata = ImageMetadataReader.readMetadata(file);
		List<GifControlDirectory> gifControlDirectories = (List<GifControlDirectory>) metadata
				.getDirectoriesOfType(GifControlDirectory.class);

		int timeLength = 0;
		if (gifControlDirectories.size() == 1) { // Do not read delay of static GIF files with single frame.
		} else if (gifControlDirectories.size() >= 1) {
			for (GifControlDirectory gifControlDirectory : gifControlDirectories) {
				try {
					if (gifControlDirectory.hasTagName(GifControlDirectory.TAG_DELAY)) {
						timeLength += gifControlDirectory.getInt(GifControlDirectory.TAG_DELAY);
					}
				} catch (MetadataException e) {
					e.printStackTrace();
				}
			}
			// Unit of time is 10 milliseconds in GIF.
			timeLength *= 10;
		}
		return timeLength;
	}
}
