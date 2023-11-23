package com.video.service;

import com.video.utils.BaseResponse;

import java.io.IOException;

public interface FilterWordsService {
    BaseResponse addFilterWords(String[] strings) throws IOException;
}
