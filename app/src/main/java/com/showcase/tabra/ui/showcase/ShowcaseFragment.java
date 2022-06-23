package com.showcase.tabra.ui.showcase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.widget.*;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.showcase.tabra.R;
import com.showcase.tabra.data.MyException;
import com.showcase.tabra.data.model.Product;
import com.showcase.tabra.data.model.Showcase;
import com.showcase.tabra.data.remote.Result;
import com.showcase.tabra.databinding.ProductListBinding;
import com.showcase.tabra.databinding.ProfileFragmentBinding;
import com.showcase.tabra.ui.login.LoginActivity;
import com.showcase.tabra.ui.product.ProductViewModel;
import com.showcase.tabra.ui.product.ProductViewModelFactory;
import com.showcase.tabra.utils.Util;

import java.util.List;

public class ShowcaseFragment extends Fragment {

    private ShowcaseViewModel mViewModel;
    private ProfileFragmentBinding binding;
    private TextView phoneTextView;
    private TextView telegramTextView;
    private String showcaseUrl;
    private TextView titleTextView;

    public static ShowcaseFragment newInstance() {
        return new ShowcaseFragment();
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new ViewModelProvider(requireActivity(), new ShowcaseViewModelFactory(getActivity().getApplication()))
                .get(ShowcaseViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = ProfileFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        titleTextView = binding.titleTextView;
        phoneTextView = binding.phoneTextView;
        telegramTextView = binding.telegramTextView;


        mViewModel = new ViewModelProvider(requireActivity(), new ShowcaseViewModelFactory(getActivity().getApplication()))
                .get(ShowcaseViewModel.class);
        mViewModel.getShowcaseLiveData().observe(getViewLifecycleOwner(), new Observer<Result<Showcase>>() {
            @Override
            public void onChanged(Result<Showcase> showcaseResult) {
                if (showcaseResult instanceof Result.Error) {
                    if (((Result.Error) showcaseResult).getError() instanceof MyException.LoginFailed401ReasonException) {
                        Util.cleanAuthToken(getContext());
                    }
                    showSearchFailed(((Result.Error) showcaseResult).getError().getError());
                }
                if (showcaseResult instanceof Result.Success) {
                    fillShowcase(((Result.Success<Showcase>) showcaseResult).getData());
                }
            }
        });



        Button logoutButton = binding.logoutButton;

        Button editButton = binding.editButton;
//        editButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("Comments");
//
//                final EditText input = new EditText(getActivity());
//
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                        LinearLayout.LayoutParams.MATCH_PARENT,
//                        LinearLayout.LayoutParams.MATCH_PARENT);
//                input.setLayoutParams(lp);
//                builder.setView(input);
//
//                // Set up the buttons
//                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // do something here on OK
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//                builder.show();
//
//            }
//        });

        Button shareLinkButton = binding.shareLinkButton;
        shareLinkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showcaseUrl==null) {
                    return;
                }
                Uri uri = Uri.parse(showcaseUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                // Verify that the intent will resolve to an activity
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Here we use an intent without a Chooser unlike the next example
                    startActivity(intent);
                }
            }
        });

        return root;
    }

    private void showSearchFailed(int error) {
        Toast.makeText(getActivity().getApplicationContext(), error, Toast.LENGTH_SHORT).show();
        login();
    }

    private void login() {
        startActivity(new Intent(getActivity(), LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
//        finish();
    }

    private void fillShowcase(Showcase showcase) {
        if (showcase==null) {
            return;
        }
        titleTextView.setText(showcase.getTitle());
        phoneTextView.setText(showcase.getContactPhone());
        telegramTextView.setText(showcase.getContactTelegram());
        showcaseUrl = showcase.getShowcaseUrl();
        if (showcase.getContactPhone()!=null) {

        }
        if (showcase.getContactTelegram()!=null) {

        }
    }
}