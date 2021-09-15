package kr.co.opensysnet.paperless.view.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.List;

import kr.co.opensysnet.paperless.view.fragment.BaseFragment;

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup rootView = (ViewGroup) inflater.inflate(layoutResID, null);
        super.setContentView(rootView);
    }

    @Override
    public void onBackPressed() {
        boolean handled = false;
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();

        for (Fragment f : fragmentList) {
            if (f instanceof BaseFragment) {
                handled = ((BaseFragment) f).onBackPressed();

                if (handled) {
                    break;
                }
            }
        }

        if (!handled) {
            super.onBackPressed();
        }
    }
}
