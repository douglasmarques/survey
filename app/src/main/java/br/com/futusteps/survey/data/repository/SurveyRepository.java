/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.futusteps.survey.data.repository;

import android.support.annotation.NonNull;

import java.util.List;

import br.com.futusteps.survey.core.survey.Survey;
import br.com.futusteps.survey.core.surveranswer.SurveyAnswer;

/**
 * Main entry point for accessing user data.
 */
public interface SurveyRepository {

    void removeCurrentSurvey();

    void addAnswer(int idQuestion, String answerText, List<Integer> alternatives);

    interface SurveysCallback {

        void onSurveySuccess(List<Survey> surveys);

        void onSurveyFail();
    }

    interface FinishSurveysCallback {

        void onFinishSuccess();

        void onFinishFail();
    }


    void surveys(@NonNull String user, @NonNull String token, SurveysCallback callback);

    void setCurrentSurvey(Survey survey);

    Survey getCurrentSurvey();

    SurveyAnswer getSurveyAnswer();

    void finishSurvey(FinishSurveysCallback callback);
}
