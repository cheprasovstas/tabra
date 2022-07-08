package com.showcase.tabra.ui.showcase;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.showcase.tabra.R;
import com.showcase.tabra.data.model.Showcase;
import com.showcase.tabra.data.remote.Result;
import com.showcase.tabra.databinding.ShowcaseEditFragmentBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowcaseEditFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowcaseEditFragment extends BottomSheetDialogFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ShowcaseEditFragmentBinding binding;
    private ShowcaseViewModel mViewModel;
    private TextInputLayout showcaseTitle;
    private TextInputLayout showcasePhone;
    private TextInputLayout showcaseTelegram;

    public ShowcaseEditFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowcaseEditFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowcaseEditFragment newInstance(String param1, String param2) {
        ShowcaseEditFragment fragment = new ShowcaseEditFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = ShowcaseEditFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        showcaseTitle = binding.showcaseTitle;
        showcasePhone = binding.showcasePhone;
        showcaseTelegram = binding.showcaseTelegram;

        mViewModel = new ViewModelProvider(requireActivity(), new ShowcaseViewModelFactory(getActivity().getApplication()))
                .get(ShowcaseViewModel.class);
        mViewModel.getShowcaseLiveData().observe(getViewLifecycleOwner(), new Observer<Result<Showcase>>() {
            @Override
            public void onChanged(Result<Showcase> result) {
                if (result instanceof Result.Error) {

                }
                if (result instanceof Result.Success) {
                    fillShowcase(((Result.Success<Showcase>) result).getData());
                }
            }
        });

        return root;
    }

    private void fillShowcase(Showcase showcase) {
        if (showcase==null) {
            return;
        }
        if (showcase.getTitle()!=null) {
            showcaseTitle.getEditText().setText(showcase.getTitle());
        }
        if (showcase.getContactPhone()!=null) {
            showcasePhone.getEditText().setText(showcase.getContactPhone());
        }
        if (showcase.getContactTelegram()!=null) {
            showcaseTelegram.getEditText().setText(showcase.getContactTelegram());
        }
    }
}