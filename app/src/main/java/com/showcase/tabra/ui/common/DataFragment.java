package com.showcase.tabra.ui.common;

import android.content.Intent;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.showcase.tabra.data.MyException;
import com.showcase.tabra.data.remote.Result;
import com.showcase.tabra.ui.login.LoginActivity;

public class DataFragment extends Fragment {

    protected void failResult(Result.Error result) {
        int error = result.getError().getError();
        if (((Result.Error) result).getError() instanceof MyException.LoginFailed401ReasonException) {
            Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            login();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        }
    }

    private void login() {
        startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//        finish();
    }
}
