package com.multitv.yuv.interfaces;

import com.multitv.yuv.models.trivia_data.TriviaContent;

import java.util.List;

/**
 * Created by cyberlinks on 13/1/17.
 */

public interface GetTriviaContentListener {

    void onTriviaSuccess(List<TriviaContent> triviaContentList);
}
