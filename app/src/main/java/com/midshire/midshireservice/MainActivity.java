package com.midshire.midshireservice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;
import com.midshire.midshireservice.utils.SignatureView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLConnection;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_CODE = 1;
    private static final int READ_EXTERNAL_STORAGE_CODE = 2;

    public int REQUEST_IMAGE_GALLERY = 1001;
    public int REQUEST_IMAGE_CAMERA = 2001;
    MaterialAlertDialogBuilder resultAlertDialog;
    ProgressDialog progressDialog;
    SignatureView signatureView;
    String serviceSerialno,customer, make, model, serialno, email, phone, engineer,
            account, warranty, cod, rentals, stdhrs, webshop, othrs, subcontract, callout,
            mcontract, delivery, installation, dateone, intimeone, outtimeone, visistDetails, partstotal, calloutstdot,
            labourhours, subtotal, vat, totalpayable, natureOfCallOut, caller ;

    EditText etcustomer, etmake, etmodel, etserialno, etemail, etphone, etengineer,
            etvisitdetails, etdateone, etIntimeone, etOuttimeone, etpartstotal, etcalloutstdot,
            etlabourhours, etsubtotal, etvat, ettotalpayable, etcaller, etnaturecallout ;
    CheckBox  chaccount, chwarranty, chcod, chrentals, chstdhrs, chwebshop, chothrs,
            chsubcontract, chcallout, chmcontract, chdelivery, chinstallation;
    Button partonebtn, parttwobtn,parttwobackbtn,partthreebtn,partthreebackbtn, btn_backtthree,btn_finish;
    LinearLayout formpartone, formparttwo, formpartthree, formpartfour,addImageSection;
    String partsOrderLines="";
    TextView txtserviceserialno;

    private Button addOrderLineButton;
    private Button submitbtn;
    private LinearLayout orderLinesContainer, fittedParts;
    RadioGroup radioGroup,imgRadioGroup;
    private EditText totalPriceEditText;
    ImageView imageViewOne, imageViewtwo, imageViewThree, imageViewFour, imgLogoutBtn;
    String sOneImage, sTwoImage, sThreeImage, sFourImage;
    String imageonestr = "";
    String imagetwostr = "";
    String imagethreestr = "";
    String imagefourstr = "";
    String signatureStr = "";
    OrderLines orderLines = new OrderLines();
    Intent starterIntent;
    boolean isOrderWithoutParts = true;

    Button btLogout;
    FirebaseAuth firebaseAuth;
    GoogleSignInClient googleSignInClient;

    boolean addImages = false;
    Button btnsigclear, btnsigsave;
    private GoogleSignInAccount acct;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        acct = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient=GoogleSignIn.getClient(this,gso);


        // Initialize firebase user
        //FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        checkPermission();
        starterIntent = getIntent();
        addOrderLineButton = findViewById(R.id.add_order_line_button);
        orderLinesContainer = findViewById(R.id.order_lines_container);
        submitbtn = findViewById(R.id.submitbtn);
        fittedParts = findViewById(R.id.fittedparts);
        radioGroup = findViewById(R.id.fittedpartsRadio);
        imgLogoutBtn = findViewById(R.id.imgLogoutBtn);

        imgLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
                ActivityCompat.finishAffinity(MainActivity.this);
            }
        });

       txtserviceserialno = findViewById(R.id.txt_serviceserialno);
        serviceSerialno = generateSerialNumber().toUpperCase();
       txtserviceserialno.setText("Sr. No: "+ serviceSerialno);
        addOrderLineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             addPartView();
            }
        });

        submitbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePartsInformation();
            }
        });
        resultAlertDialog = new MaterialAlertDialogBuilder(MainActivity.this);
        progressDialog = new ProgressDialog(MainActivity.this);

        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Fetching details...");

        etcustomer = findViewById(R.id.et_customer);
        etmake = findViewById(R.id.et_make);
        etmodel = findViewById(R.id.et_model);
        etserialno = findViewById(R.id.et_serialno);
        etemail = findViewById(R.id.et_email);
        etphone = findViewById(R.id.et_telephone);
        etengineer = findViewById(R.id.et_engineer);
        etnaturecallout = findViewById(R.id.et_naturecallout);
        etcaller = findViewById(R.id.et_caller);
        etdateone = findViewById(R.id.et_dateone);
        etIntimeone = findViewById(R.id.et_visitIntimeone);
        etOuttimeone = findViewById(R.id.et_visitOuttimeone);
        etvisitdetails = findViewById(R.id.et_dateonevisit);
        etpartstotal = findViewById(R.id.et_partstotal);
        etcalloutstdot = findViewById(R.id.et_callouttotal);
        etlabourhours = findViewById(R.id.et_labourHours);
        etsubtotal = findViewById(R.id.et_subtotal);
        etvat = findViewById(R.id.et_vat);
        ettotalpayable = findViewById(R.id.et_finaltotalamount);

        formpartone = findViewById(R.id.formpartone);
        formparttwo = findViewById(R.id.formparttwo);
        formpartthree = findViewById(R.id.formpartthree);
        formpartfour = findViewById(R.id.formpartfour);
        partonebtn = findViewById(R.id.btn_partone);
        parttwobtn = findViewById(R.id.btn_parttwo);
        parttwobackbtn = findViewById(R.id.btn_backone);

        partthreebtn = findViewById(R.id.btn_partthree);
        partthreebackbtn = findViewById(R.id.btn_backtwo);

        btn_backtthree = findViewById(R.id.btn_backtthree);
        btn_finish = findViewById(R.id.btn_finish);

        imgRadioGroup = findViewById(R.id.addImagesRadio);
        addImageSection = findViewById(R.id.addimageSection);

        imageViewOne = findViewById(R.id.imageViewOne);
        imageViewtwo = findViewById(R.id.imageViewTwo);
        imageViewThree = findViewById(R.id.imageViewThree);
        imageViewFour = findViewById(R.id.imageViewFour);

        chaccount = findViewById(R.id.ch_account);
        chwarranty = findViewById(R.id.ch_warranty);
        chcod = findViewById(R.id.ch_cod);
        chrentals = findViewById(R.id.ch_rentals);
        chstdhrs = findViewById(R.id.ch_stdhrs);
        chwebshop = findViewById(R.id.ch_webshop);
        chothrs = findViewById(R.id.ch_othrs);
        chcallout = findViewById(R.id.ch_callout);
        chsubcontract = findViewById(R.id.ch_subcontract);
        chmcontract = findViewById(R.id.ch_mcontract);
        chdelivery = findViewById(R.id.ch_delivery);
        chinstallation = findViewById(R.id.ch_installation);
        signatureView = findViewById(R.id.signatureView);
        btnsigsave = findViewById(R.id.button_sigsave);
        btnsigclear = findViewById(R.id.button_sigclear);
        btnsigclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signatureView.clearSignature();
            }
        });
        btnsigsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap signatureBitmap = signatureView.getSignatureBitmap();
                signatureBitmap = optimizeBitmap(signatureBitmap);
                String bitmapstr = bitmapToBase64(signatureBitmap);
                String mimetype = getMimeType(signatureBitmap);
                signatureStr = "data:"+mimetype+";base64,"+bitmapstr;
            }
        });


        etdateone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MainActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                                etdateone.setText(i2+"/"+(i1+1)+"/"+i);
                                dateone = i2+"/"+(i1+1)+"/"+i;
                            }
                        },year,month,day
                );
                datePickerDialog.show();
            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.radio_button_show){
                    fittedParts.setVisibility(View.VISIBLE);
                    isOrderWithoutParts = true;
                } else if (i == R.id.radio_button_hide) {
                    fittedParts.setVisibility(View.GONE);
                    isOrderWithoutParts = false;
                }
            }
        });

        imgRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == R.id.img_show) {
                    addImageSection.setVisibility(View.VISIBLE);
                    addImages = true;
                } else if (i == R.id.img_hide) {
                    addImageSection.setVisibility(View.GONE);
                    addImages = false;
                    imageonestr = "";
                    imagetwostr = "";
                    imagethreestr = "";
                    imagefourstr = "";
                }
            }
        });

        partonebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formpartone.setVisibility(View.GONE);
                formparttwo.setVisibility(View.VISIBLE);
            }
        });
        parttwobackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formparttwo.setVisibility(View.GONE);
                formpartone.setVisibility(View.VISIBLE);
            }
        });
        parttwobtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (addImages == true) {
                    if((imageonestr!="" && imageonestr!=null) || (imagetwostr!="" && imagetwostr!=null) || (imagethreestr!="" && imagethreestr!=null) || (imagefourstr!="" && imagefourstr!=null)){
                        formparttwo.setVisibility(View.GONE);
                        formpartthree.setVisibility(View.VISIBLE);
                    }
                    else{
                        Toast.makeText(MainActivity.this, "Please add atleast 1 image", Toast.LENGTH_LONG).show();
                    }
                } else {
                    formparttwo.setVisibility(View.GONE);
                    formpartthree.setVisibility(View.VISIBLE);
                }
            }
        });
        partthreebackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formparttwo.setVisibility(View.VISIBLE);
                formpartthree.setVisibility(View.GONE);
            }
        });
        btn_backtthree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formpartthree.setVisibility(View.VISIBLE);
                formpartfour.setVisibility(View.GONE);
            }
        });

        partthreebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                formpartthree.setVisibility(View.GONE);
                formpartfour.setVisibility(View.VISIBLE);
            }
        });

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(signatureStr.length()>0) {
                    sendServiceReport();
                }
                else {
                    Toast.makeText(MainActivity.this, "Please sign and save first!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imageViewOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
                chooser.putExtra(Intent.EXTRA_TITLE, "Select from:");

                Intent[] intentArray = {cameraIntent};
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooser, 10001);
            }
        });

        imageViewtwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
                chooser.putExtra(Intent.EXTRA_TITLE, "Select from:");

                Intent[] intentArray = {cameraIntent};
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooser, 10002);
            }
        });

        imageViewThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
                chooser.putExtra(Intent.EXTRA_TITLE, "Select from:");

                Intent[] intentArray = {cameraIntent};
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooser, 10003);
            }
        });

        imageViewFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");

                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                Intent chooser = new Intent(Intent.ACTION_CHOOSER);
                chooser.putExtra(Intent.EXTRA_INTENT, galleryIntent);
                chooser.putExtra(Intent.EXTRA_TITLE, "Select from:");

                Intent[] intentArray = {cameraIntent};
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
                startActivityForResult(chooser, 10004);
            }
        });


    }

    private void sendServiceReport() {
        customer = etcustomer.getText().toString().trim();
        natureOfCallOut = etnaturecallout.getText().toString().trim();
        make = etmake.getText().toString().trim();
        model = etmodel.getText().toString().trim();
        serialno = etserialno.getText().toString().trim();
        caller = etcaller.getText().toString().trim();
        email = etemail.getText().toString().trim();
        phone = etphone.getText().toString().trim();
        engineer = etengineer.getText().toString().trim();
        intimeone = etIntimeone.getText().toString().trim();
        outtimeone = etOuttimeone.getText().toString().trim();
        visistDetails = etvisitdetails.getText().toString().trim();
        partstotal = etpartstotal.getText().toString().trim();
        calloutstdot = etcalloutstdot.getText().toString().trim();
        labourhours = etlabourhours.getText().toString().trim();
        subtotal = etsubtotal.getText().toString().trim();
        vat = etvat.getText().toString().trim();
        totalpayable = ettotalpayable.getText().toString().trim();

        ServiceReportModel serviceReportModel = new ServiceReportModel();
        serviceReportModel.setServiceSerialNo(serviceSerialno);
        serviceReportModel.setCustomer(customer);
        serviceReportModel.setNatureOfCallOut(natureOfCallOut);
        serviceReportModel.setMake(make);
        serviceReportModel.setModel(model);
        serviceReportModel.setSerialno(serialno);
        serviceReportModel.setCaller(caller);
        serviceReportModel.setEmail(email);
        serviceReportModel.setPhone(phone);
        serviceReportModel.setEngineer(engineer);
        serviceReportModel.setAccount(String.valueOf(chaccount.isChecked()));
        serviceReportModel.setWarranty(String.valueOf(chwarranty.isChecked()));
        serviceReportModel.setCod(String.valueOf(chcod.isChecked()));
        serviceReportModel.setRentals(String.valueOf(chrentals.isChecked()));
        serviceReportModel.setStdhrs(String.valueOf(chstdhrs.isChecked()));
        serviceReportModel.setWebshop(String.valueOf(chwebshop.isChecked()));
        serviceReportModel.setOthrs(String.valueOf(chothrs.isChecked()));
        serviceReportModel.setCallout(String.valueOf(chcallout.isChecked()));
        serviceReportModel.setSubcontract(String.valueOf(chsubcontract.isChecked()));
        serviceReportModel.setMcontract(String.valueOf(chmcontract.isChecked()));
        serviceReportModel.setDelivery(String.valueOf(chdelivery.isChecked()));
        serviceReportModel.setInstallation(String.valueOf(chinstallation.isChecked()));
        serviceReportModel.setVisitdate(dateone);
        serviceReportModel.setInTime(intimeone);
        serviceReportModel.setOutTime(outtimeone);
        serviceReportModel.setVisitDetails(visistDetails);
        serviceReportModel.setPartsTotal(partstotal);
        serviceReportModel.setCalloutstdot(calloutstdot);
        serviceReportModel.setLabourHrs(labourhours);
        serviceReportModel.setSubtotal(subtotal);
        serviceReportModel.setVat(vat);
        serviceReportModel.setTotalpayable(totalpayable);
        Gson gson = new Gson();
        String jsonstring = gson.toJson(serviceReportModel);

//        Log.d("REPORTSSSSSS:",jsonstring);
        try {
            JSONObject jsonObject = new JSONObject(jsonstring);
            if(orderLinesContainer.getChildCount()!=orderLines.getSize()){
                Toast.makeText(this, "Please save fitted parts", Toast.LENGTH_LONG).show();
            }
            else {
                sendJsonPostRequest(jsonObject);
            }

        }catch (JSONException err){
            Log.d("Error", err.toString());
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check condition
        if (requestCode == 10001 && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                bitmap = optimizeBitmap(bitmap);
                imageViewOne.setImageBitmap(bitmap);
                String mimetype = getMimeType(bitmap);
                imageonestr="data:"+mimetype+";base64,"+bitmapToBase64(bitmap);
            } else {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                photo = optimizeBitmap(photo);
                imageViewOne.setImageBitmap(photo);
                String mimetype = getMimeType(photo);
                imageonestr="data:"+mimetype+";base64,"+bitmapToBase64(photo);
            }
        }
        if (requestCode == 10002 && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                bitmap = optimizeBitmap(bitmap);
                imageViewtwo.setImageBitmap(bitmap);
                String mimetype = getMimeType(bitmap);
                imagetwostr="data:"+mimetype+";base64,"+bitmapToBase64(bitmap);
            } else {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                photo = optimizeBitmap(photo);
                imageViewtwo.setImageBitmap(photo);
                String mimetype = getMimeType(photo);
                imagetwostr="data:"+mimetype+";base64,"+bitmapToBase64(photo);
            }
        }
        if (requestCode == 10003 && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                bitmap = optimizeBitmap(bitmap);
                imageViewThree.setImageBitmap(bitmap);
                String mimetype = getMimeType(bitmap);
                imagethreestr="data:"+mimetype+";base64,"+bitmapToBase64(bitmap);
            } else {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                photo = optimizeBitmap(photo);
                imageViewThree.setImageBitmap(photo);
                String mimetype = getMimeType(photo);
                imagethreestr="data:"+mimetype+";base64,"+bitmapToBase64(photo);
            }
        }
        if (requestCode == 10004 && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri selectedImageUri = data.getData();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                bitmap = optimizeBitmap(bitmap);
                imageViewFour.setImageBitmap(bitmap);
                String mimetype = getMimeType(bitmap);
                imagefourstr="data:"+mimetype+";base64,"+bitmapToBase64(bitmap);
            } else {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                photo = optimizeBitmap(photo);
                imageViewFour.setImageBitmap(photo);
                String mimetype = getMimeType(photo);
                imagefourstr="data:"+mimetype+";base64,"+bitmapToBase64(photo);
            }
        }
    }


    public static String generateSerialNumber() {
        Random random = new Random();
        String prefix = "MCE";
        String serialNumber = prefix;

        for (int i = 0; i < 10; i++) {
            serialNumber += random.nextInt(10);
        }

        return serialNumber;
    }


    public void checkPermission(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, READ_EXTERNAL_STORAGE_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission is granted, proceed with camera functionality
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Camera permission is denied, display a message to the user
                //Toast.makeText(this, "Camera permission is required to use the app", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Read external storage permission is granted, proceed with read external storage functionality
                Toast.makeText(this, "Read external storage permission granted", Toast.LENGTH_SHORT).show();
            } else {
                // Read external storage permission is denied, display a message to the user
                //Toast.makeText(this, "Read external storage permission is required to use the app", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void addPartView() {
        final View partView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.parts_order_line, null);
        final EditText quantityEditText = partView.findViewById(R.id.quantity_edit_text);
        final EditText partNoEditText = partView.findViewById(R.id.name_edit_text);
        final EditText partsDescriptionEditText = partView.findViewById(R.id.description_edit_text);
        final EditText unitPriceEditText = partView.findViewById(R.id.unit_price_edit_text);
        final EditText extPriceEditText = partView.findViewById(R.id.ext_price_edit_text);
        final ImageView deletePartButton = partView.findViewById(R.id.imgdltbtn);

        deletePartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                orderLinesContainer.removeView(partView);
            }
        });

        orderLinesContainer.addView(partView);
    }

    private void savePartsInformation() {
        ArrayList<PartsModel> partsList = new ArrayList<>();
        for (int i = 0; i < orderLinesContainer.getChildCount(); i++) {
            View partView = orderLinesContainer.getChildAt(i);
            EditText quantityEditText = partView.findViewById(R.id.quantity_edit_text);
            EditText partNoEditText = partView.findViewById(R.id.name_edit_text);
            EditText partsDescriptionEditText = partView.findViewById(R.id.description_edit_text);
            EditText unitPriceEditText = partView.findViewById(R.id.unit_price_edit_text);
            EditText extPriceEditText = partView.findViewById(R.id.ext_price_edit_text);

            String quantity = quantityEditText.getText().toString();
            String partNo = partNoEditText.getText().toString();
            String partsDescription = partsDescriptionEditText.getText().toString();
            String unitPrice = unitPriceEditText.getText().toString();
            String extPrice = extPriceEditText.getText().toString();

            PartsModel partsModel = new PartsModel(quantity, partNo, partsDescription, unitPrice, extPrice);
            partsList.add(partsModel);
        }
        orderLines.setPartsList(partsList);
        Gson gson = new Gson();
        partsOrderLines = gson.toJson(orderLines);
        Toast.makeText(this, "Parts Saved!", Toast.LENGTH_SHORT).show();

    }
    private void sendJsonPostRequest(JSONObject jsonObject){
        progressDialog.show();

        if(isOrderWithoutParts == false || partsOrderLines.equals("")){
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    "https://midshire.mobyottadevelopers.online/reportsNoParts.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Log.d("IMAGESSSSS",imageonestr);
                            progressDialog.dismiss();
                            resultAlertDialog
                                    .setTitle("Submitted")
                                    .setMessage(" Service report for "+serviceSerialno+" has been sent to email.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            startActivity(starterIntent);
                                        }
                                    }).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //Log.d("IMAGESSSSS",imageonestr.length()+", "+imagetwostr.length()+", "+imagethreestr.length()+", "+imagefourstr.length()+", ");
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Error Submitting report", Toast.LENGTH_LONG).show();
                    Log.d("REGERR",""+error.getMessage());
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
                @Override
                protected Map<String,String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("serviceimgone",imageonestr);
                    params.put("serviceimgtwo",imagetwostr);
                    params.put("serviceimgthree",imagethreestr);
                    params.put("serviceimgfour",imagefourstr);
                    params.put("servicedetails",jsonObject.toString());
                    params.put("signatureImage",signatureStr);

                    return params;
                }
            };
            queue.add(stringRequest);
        }
        else {
            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

            StringRequest stringRequest = new StringRequest(
                    Request.Method.POST,
                    "https://midshire.mobyottadevelopers.online/report.php",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            resultAlertDialog
                                    .setTitle("Submitted")
                                    .setMessage(" Service report for "+serviceSerialno+" has been sent to email.")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                            startActivity(starterIntent);
                                        }
                                    }).show();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Toast.makeText(MainActivity.this, "Error Submitting report", Toast.LENGTH_LONG).show();
                    Log.d("REGERR",""+error.getMessage());
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("Content-Type","application/x-www-form-urlencoded");
                    return params;
                }
                @Override
                protected Map<String,String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<String, String>();
                    params.put("serviceimgone",imageonestr);
                    params.put("serviceimgtwo",imagetwostr);
                    params.put("serviceimgthree",imagethreestr);
                    params.put("serviceimgfour",imagefourstr);
                    params.put("serviceparts",partsOrderLines);
                    params.put("servicedetails",jsonObject.toString());
                    params.put("signatureImage",signatureStr);

                    return params;
                }
            };
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    5*1000, 2, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            ));
            queue.add(stringRequest);
        }

    }
    public void signOut(){
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                startActivity(new Intent(MainActivity.this,MidshireLogin.class));
                acct=null;
                finish();

            }
        });
    }

    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String base64String = Base64.encodeToString(byteArray, Base64.DEFAULT);
        byteArrayOutputStream.reset();
        return base64String;
    }

    public static String getMimeType(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();

        String mimeType;
        try {
            mimeType = URLConnection.guessContentTypeFromStream(new ByteArrayInputStream(byteArray));
        } catch (IOException e) {
            e.printStackTrace();
            mimeType = null;
        }

        return mimeType;
    }

    public Bitmap optimizeBitmap(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int maxWidth = 800;
        int maxHeight = 800;
        if (width > maxWidth || height > maxHeight) {
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;
            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
        }
        return bitmap;
    }


}