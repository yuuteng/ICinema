package yuut.icinema.ui.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import yuut.icinema.R;
import yuut.icinema.support.Util.PhoneFormatCheckUtils;

/**
 * Created by yuut on 2017/4/26.
 */

public class RegisterActivity extends BaseActivity {

    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.cv_add)
    CardView cvAdd;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.et_username)
    EditText usernameView;
    @Bind(R.id.et_password)
    EditText passwordView;
    @Bind(R.id.et_repeatpassword)
    EditText rePasswordView;
    @Bind(R.id.bt_go)
    Button registerBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        initToolBar(toolbar, "Register", R.mipmap.icon_arrow_back);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
                ;
            }
        });
    }

    @OnClick(R.id.bt_go)
    public void onRegisterClicked() {
        String username = usernameView.getText().toString();
        String password = passwordView.getText().toString();
        String rePassword = rePasswordView.getText().toString();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "请填写E-mail", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)) {
            Toast.makeText(this, "请填写密码", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!PhoneFormatCheckUtils.isChinaPhoneLegal(username)) {
            Toast.makeText(this, "手机号码格式错误", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password.equals(rePassword)) {
            Toast.makeText(this, "两次密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
//        SceneFactory.getSceneService().register(username, password)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(Schedulers.io())
//                .subscribe(new Subscriber<AnsEntity>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Logger.d(e.getMessage());
//                    }
//
//                    @Override
//                    public void onNext(AnsEntity ansEntity) {
//                        if (ansEntity.isSuccess()) {
//                            SharePreUtil.saveIntData(RegisterActivity.this, "userId", ansEntity.getUserId());
//                            finish();
//                        }
//                    }
//                });
    }


    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }

        });
    }

    @OnClick(R.id.fab)
    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.mipmap.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                animateRevealClose();
                break;
        }
        return false;
    }

}
