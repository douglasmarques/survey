package br.com.futusteps.survey.ui.questions;

import android.support.annotation.NonNull;

import java.util.List;

import br.com.futusteps.survey.data.repository.SurveyRepository;
import br.com.futusteps.survey.util.StringUtils;
import br.com.futusteps.R;
import br.com.futusteps.survey.core.survey.Question;

public class QuestionPresenter implements QuestionContract.UserActionsListener{

    @NonNull
    private final QuestionContract.View mLoginView;

    @NonNull
    private final SurveyRepository mRepository;

    public QuestionPresenter(@NonNull QuestionContract.View loginView,
                             @NonNull SurveyRepository repository) {
        mLoginView = loginView;
        mRepository = repository;
    }

    @Override
    public void loadNextQuestion(Question question, int position, int total, String answer, List<Integer> alternatives) {
        if(validateQuestion(question, answer, alternatives)) {
            if (question.getType().equalsIgnoreCase("text")){
                mRepository.addAnswer(question.getId(), answer, null);
            }else if(question.getType().equalsIgnoreCase("single")){
                mRepository.addAnswer(question.getId(), null, alternatives);
            }
            if (position < total - 1) {
                mLoginView.showNextQuestion(question, position, total);
            } else {
                mLoginView.showUserDataForm();
            }
        }
    }

    private boolean validateQuestion(Question question, String answer, List<Integer> alternatives){
        if (question.getType().equalsIgnoreCase("text") && StringUtils.isBlank(answer)) {
            mLoginView.showValidationError(R.string.error_empty_answer);
            return false;
        }else if(question.getType().equalsIgnoreCase("single") &&
                (alternatives == null ||
                alternatives.get(0) == null ||
                alternatives.get(0) < 1)){
            mLoginView.showValidationError(R.string.error_no_alternative);
            return false;
        }

        return true;
    }
}
