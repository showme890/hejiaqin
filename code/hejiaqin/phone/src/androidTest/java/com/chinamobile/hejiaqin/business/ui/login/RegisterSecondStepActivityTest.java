package com.chinamobile.hejiaqin.business.ui.login;

import android.content.Intent;
import android.os.Message;
import android.test.ActivityUnitTestCase;
import android.widget.Button;
import android.widget.EditText;

import com.chinamobile.hejiaqin.R;
import com.chinamobile.hejiaqin.business.BussinessConstants;
import com.chinamobile.hejiaqin.business.model.FailResponse;
import com.chinamobile.hejiaqin.business.model.login.req.RegisterSecondStepInfo;
import com.chinamobile.hejiaqin.business.ui.basic.view.HeaderView;

/**
 * Created by Administrator on 2017/4/24 0024.
 */
public class RegisterSecondStepActivityTest extends
        ActivityUnitTestCase<RegisterSecondStepActivity> {
    private RegisterSecondStepActivity mActivity;

    private Button registerActionBtn;
    private EditText passwordEt;
    private EditText confirmPwdEt;
    private HeaderView mHeaderView;

    public RegisterSecondStepActivityTest() {
        super(RegisterSecondStepActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        RegisterSecondStepInfo registerSecondStepInfo = new RegisterSecondStepInfo();
        registerSecondStepInfo.setPhone("12346678");
        registerSecondStepInfo.setCode("123456");
        Intent intent = new Intent(getInstrumentation().getTargetContext(),
                RegisterSecondStepActivity.class);
        intent.putExtra(BussinessConstants.Login.REGISTER_SECOND_STEP_INFO_KEY,
                registerSecondStepInfo);
        startActivity(intent, null, null);

        mActivity = getActivity();
        assertNotNull(mActivity);

        mHeaderView = (HeaderView) mActivity.findViewById(R.id.header);
        registerActionBtn = (Button) mActivity.findViewById(R.id.reset_action_button);
        passwordEt = (EditText) mActivity.findViewById(R.id.password_et);
        confirmPwdEt = (EditText) mActivity.findViewById(R.id.repeat_password_et);
    }

    public void testInitView() {
        assertNotNull(mHeaderView);
        assertNotNull(registerActionBtn);
        assertNotNull(passwordEt);
        assertNotNull(confirmPwdEt);
    }

    public void testOnClick() {
        mHeaderView.backImageView.performClick();
    }

    public void testHandleStateMessage() {

        mActivity.handleStateMessage(generateMessage(BussinessConstants.LoginMsgID.REGISTER_SECOND_STEP_SUCCESS_MSG_ID));

        FailResponse failResponse = new FailResponse();
        failResponse.setCode("123");
        mActivity.handleStateMessage(generateMessage(BussinessConstants.LoginMsgID.REGISTER_FIRST_STEP_FAIL_MSG_ID, failResponse));

    }

    private Message generateMessage(int what) {
        return generateMessage(what, null);
    }

    private Message generateMessage(int what, Object obj) {
        Message message = Message.obtain();
        message.what = what;
        message.obj = obj;
        return message;
    }
}
