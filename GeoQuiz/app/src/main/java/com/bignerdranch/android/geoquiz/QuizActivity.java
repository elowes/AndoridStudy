package com.bignerdranch.android.geoquiz;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;

public class QuizActivity extends AppCompatActivity {
    private static final String TAG = "QuizActivity";
    private static final String KEY_INDEX = "index";
    private static final int REQUEST_CODE_CHEAT = 0;

    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mPrevButton;
    private ImageButton mNextButton;
    private Button mCheatButton;
    private TextView mQuestionTextView;


    private Question[] mQuestionBank = new Question[] {
            new Question(R.string.question_australia, true),
            new Question(R.string.question_oceans, true),
            new Question(R.string.question_mideast, false),
            new Question(R.string.question_africa, false),
            new Question(R.string.question_americas, true),
            new Question(R.string.question_asia, true)
    };

    private int mCurrentIndex = 0;
    private boolean mIsCheater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_quiz);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX);
        }

        mQuestionTextView = (TextView) findViewById(R.id.question_text_view);

        // 挑战练习，为 TextView 新增监听器
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextQuestion();
            }
        });

        mTrueButton = (Button) findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(true);
            }
        });
        mFalseButton = (Button) findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAnswer(false);
            }
        });

        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prevQuestion();
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextQuestion();
            }
        });

        mCheatButton = (Button) findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start CheatActivity
//                Intent intent = new Intent(QuizActivity.this, CheatActivity.class);
                boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
                Intent intent = CheatActivity.newIntent(QuizActivity.this, answerIsTrue);
//                startActivity(intent);
                startActivityForResult(intent, REQUEST_CODE_CHEAT);
            }
        });

        updateQuestion();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            if (data == null) {
                return;
            }
            mIsCheater = CheatActivity.wasAnswerShown(data);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");
        outState.putInt(KEY_INDEX, mCurrentIndex);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestory() called");
    }

    private void prevQuestion() {
        if (mCurrentIndex == 0) {
            mCurrentIndex = mQuestionBank.length;
        }
        mCurrentIndex = (mCurrentIndex - 1) % mQuestionBank.length;
        updateQuestion();
    }

    private void nextQuestion() {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        updateQuestion();
    }
    private void setBtnsStatus(boolean enabled) {
        mTrueButton.setEnabled(enabled);
        mFalseButton.setEnabled(enabled);
    }

    private void updateQuestion() {
        mIsCheater = false;
        int question = mQuestionBank[mCurrentIndex].getTextResId();
        boolean questionAnswered = mQuestionBank[mCurrentIndex].isAnswered();
        setBtnsStatus(!questionAnswered);

        mQuestionTextView.setText(question);
    }

    private void checkResults() {
        int num = 0, correctNum = 0;
        for (Question q: mQuestionBank) {
            if (q.isAnswered()) {
                num++;
                if (q.uAreRight()) {
                    correctNum++;
                }
            }
        }
        if (num < mQuestionBank.length) {
            Toast t = Toast.makeText(this, "您已回答了" + num + "题，总共有" + mQuestionBank.length + "题。", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();
        } else {
            DecimalFormat df = new DecimalFormat("0.00");
            Log.d(TAG, String.valueOf(correctNum));
            Toast t = Toast.makeText(this, "您的正确率为" + df.format((float)correctNum / mQuestionBank.length * 100) + "%。", Toast.LENGTH_SHORT);
            t.setGravity(Gravity.TOP, 0, 0);
            t.show();
        }
    }


    private void checkAnswer(boolean userPressedTrue) {
        Question currentQuestion = mQuestionBank[mCurrentIndex];
        boolean answerIsTrue = currentQuestion.isAnswerTrue();
        currentQuestion.setAnswered(userPressedTrue); // 当前题目设置已回答标识
        setBtnsStatus(false);
        checkResults();

        int messageResId = 0;

        if (mIsCheater) {
            messageResId = R.string.judgment_toast;
        } else {
            if (userPressedTrue == answerIsTrue) {
                messageResId = R.string.correct_toast;
            } else {
                messageResId = R.string.incorrect_toast;
            }
        }

        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show();
    }
}
