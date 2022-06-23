package com.showcase.tabra.ui.product;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.showcase.tabra.BuildConfig;
import com.showcase.tabra.R;
import com.showcase.tabra.databinding.ProductDetailsBinding;
import com.showcase.tabra.utils.PictureUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;
import com.showcase.tabra.data.model.Product;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailsFragment extends Fragment {

    private ProductViewModel viewModel;
    private Bitmap newImage;
    private ImageView imageView;
    private TextInputLayout nameTxt, priceTxt, descriptionTxt;
    private TextInputLayout unitPrice;
    private Switch active;

    private ProductDetailsBinding binding;

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private FloatingActionButton fab;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = ProductDetailsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Create Toolbar
        Toolbar toolbar = binding.toolbarProductDetails.productDetailsToolbar;
        toolbar.inflateMenu(R.menu.product_details_menu);
        ImageButton cancelButton = binding.toolbarProductDetails.toolbarCancelButton;
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_product_details_save) {
                    Product product = viewModel.getProduct().getValue();

                    product.setName(nameTxt.getEditText().getText().toString());
                    if (priceTxt.getEditText() != null & !"".equals(priceTxt.getEditText().getText().toString())) {
                        product.setPrice(Float.valueOf(priceTxt.getEditText().getText().toString()).floatValue());
                    }
                    if (unitPrice.getEditText() != null) {
                        product.setUnitPrice(unitPrice.getEditText().getText().toString());
                    }
                    product.setActive(active.isChecked());
                    if (descriptionTxt.getEditText() != null) {
                        product.setDescription(descriptionTxt.getEditText().getText().toString());
                    }
                    if (newImage!=null) {
                        product.setF(PictureUtils.getImage(PictureUtils.resizeBitmap(newImage, 400, 400), getContext().getCacheDir()));
                    }
                    saveProduct(product);
                }
                if (id == R.id.action_product_details_delete) {
                    Product product = viewModel.getProduct().getValue();

                    deleteProduct(product);
                }
                if (id == R.id.action_product_details_share) {
                    Product product = viewModel.getProduct().getValue();

                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.setType("text/plain");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, BuildConfig.API_URL+"products/"+product.getId().toString());
                    //startActivity(sendIntent);
                    startActivity(Intent.createChooser(sendIntent, "Title"));
                }

                return false;
            }
        });

        // Create viewModel
        viewModel = new ViewModelProvider(requireActivity(), new ProductViewModelFactory(getActivity().getApplication()))
                .get(ProductViewModel.class);
        viewModel.getProductFormState().observe(getViewLifecycleOwner(), new Observer<ProductFormState>() {
            @Override
            public void onChanged(@Nullable ProductFormState productFormState) {
                if (productFormState == null) {
                    return;
                }
                toolbar.getMenu().findItem(R.id.action_product_details_save).setEnabled(productFormState.isDataValid());
            }
        });


        fab = binding.floatingActionButtonAddPictire;
        fab.bringToFront();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestPermissions(getActivity())){
                    chooseImage(getContext());
                }
            }
        });

        this.nameTxt = binding.productNameDetails;
        this.priceTxt = binding.productPriceDetails;
        this.descriptionTxt = binding.productDescriptionDetails;
        this.imageView = binding.productImageDetails;
        this.unitPrice = binding.productUnitPriceDetails;
        this.active = binding.productActiveDetails;

        //View model
        viewModel.getProduct().observe(getViewLifecycleOwner(), new Observer<Product>() {
            @Override
            public void onChanged(Product product) {
                if (product!= null) {
                    fillProduct(product);
                    viewModel.productDataChanged(nameTxt.getEditText().getText().toString());
                }
            }
        });


        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.productDataChanged(nameTxt.getEditText().getText().toString());
            }
        };
        this.nameTxt.getEditText().addTextChangedListener(afterTextChangedListener);
//        this.priceTxt.getEditText().addTextChangedListener(afterTextChangedListener);
//        nameTxt.getEditText().setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_DONE) {
//                    loginViewModel.login(usernameEditText.getText().toString(),
//                            passwordEditText.getText().toString());
//                }
//                return false;
//            }
//        });


        return root;
    }


    private void fillProduct(Product product) {
        if (product.getName() != null) {
            nameTxt.getEditText().setText(product.getName());
        }
        if (product.getPrice() != null) {
            priceTxt.getEditText().setText(product.getPrice().toString());
        }
        if (product.getUnitPrice() != null) {
            unitPrice.getEditText().setText(product.getUnitPrice());
        }
        if (product.getDescription() != null) {
            descriptionTxt.getEditText().setText(product.getDescription());
        }
        Picasso.get().load(product.getImage()).fit().centerCrop()
                .placeholder(R.drawable.product_list_placeholder)
                //               .error(R.drawable.user_placeholder_error)
                .into(imageView);
        active.setChecked(product.isActive());

    }


    private void saveProduct(Product product) {
        hideKeyboard();
        Bundle result = new Bundle();
        if (product.getId()!=null) {
            viewModel.editProduct(product);
            getParentFragmentManager().setFragmentResult("edit_product", result);
        } else {
            viewModel.addProduct(product);
            getParentFragmentManager().setFragmentResult("add_product", result);
        }
        // The child fragment needs to still set the result on its parent fragment manager

        getActivity().onBackPressed();
    }

    private void deleteProduct(Product product) {
        viewModel.deleteProduct(product);
        hideKeyboard();


        Bundle result = new Bundle();
        result.putString("id", product.getId().toString());
        // The child fragment needs to still set the result on its parent fragment manager
        getParentFragmentManager().setFragmentResult("delete_product", result);

        getActivity().onBackPressed();
    }

    public void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(),
                                    "FlagUp Requires Access to Camara.", Toast.LENGTH_SHORT)
                            .show();
                } else if (ContextCompat.checkSelfPermission(getContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getContext(),
                            "FlagUp Requires Access to Your Storage.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    chooseImage(getContext());
                }
                break;
        }
    }

    // function to check permission
    public static boolean checkAndRequestPermissions(final Activity context) {
        int WExtstorePermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (WExtstorePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded
                    .add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(context, listPermissionsNeeded
                            .toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    // function to let's the user to choose image from camera or gallery
    private void chooseImage(Context context){
        final CharSequence[] optionsMenu = {getResources().getString(R.string.product_details_action_take_photo)
                , getResources().getString(R.string.product_details_action_choose_from)
                , getResources().getString(R.string.product_details_action_exit) }; // create a menuOption Array
        // create a dialog for showing the optionsMenu
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // set the items in builder
        builder.setItems(optionsMenu, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(optionsMenu[i].equals(getResources().getString(R.string.product_details_action_take_photo))){
                    // Open the camera and get the photo
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureActivityResultLauncher.launch(takePicture);
                }
                else if(optionsMenu[i].equals(getResources().getString(R.string.product_details_action_choose_from))){
                    // choose from  external storage
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    pickPhotoActivityResultLauncher.launch(pickPhoto);
                }
                else if (optionsMenu[i].equals(getResources().getString(R.string.product_details_action_exit))) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }


    // You can do the assignment inside onAttach or onCreate, i.e, before the activity is displayed
    ActivityResultLauncher<Intent> takePictureActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                        setNewImage(bitmap);
                    }
                }
            });

    private void setNewImage(Bitmap bitmap) {
        newImage = bitmap;

        Picasso.get().load(PictureUtils.getImage(PictureUtils.resizeBitmap(bitmap, 400, 400), getContext().getCacheDir()))
                .fit()
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .centerCrop()
                .into(imageView);
    }

    ActivityResultLauncher<Intent> pickPhotoActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();

                        InputStream is = null;
                        try {
                            is = getActivity().getContentResolver().openInputStream(data.getData());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        setNewImage(bitmap);
                    }
                }
            }
            );

}


