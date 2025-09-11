package com.eyadalalimi.students.ui.activity.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.eyadalalimi.students.R;
import com.eyadalalimi.students.core.base.BaseActivity;
import com.eyadalalimi.students.databinding.ActivityRegisterBinding;
import com.eyadalalimi.students.model.College;
import com.eyadalalimi.students.model.Country;
import com.eyadalalimi.students.model.Major;
import com.eyadalalimi.students.model.University;
import com.eyadalalimi.students.repo.ApiCallback;
import com.eyadalalimi.students.repo.AuthRepository;
import com.eyadalalimi.students.repo.StructureRepository;
import com.eyadalalimi.students.response.TokenResponse;

import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends BaseActivity {
    private ActivityRegisterBinding binding;
    private AuthRepository authRepo;
    private StructureRepository structRepo;

    private final List<Country> countries = new ArrayList<>();
    private final List<University> universities = new ArrayList<>();
    private final List<College> colleges = new ArrayList<>();
    private final List<Major> majors = new ArrayList<>();

    private Integer countryId = null;
    private Integer universityId = null;
    private Integer collegeId = null;
    private Integer majorId = null;
    private Integer level = null;
    private String  gender = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authRepo = new AuthRepository(this);
        structRepo = new StructureRepository(this);
        setupToolbar(binding.toolbar, getString(R.string.auth_register), true);

        initStaticSpinners();
        setupCountries();
        setupUniversities();
        setupColleges();
        setupMajors();

        binding.btnRegister.setOnClickListener(v -> doRegister());
    }

    // مستويات ثابتة 1..8 + غير محدد ; الجنس ثابت
    private void initStaticSpinners() {
        // Level
        List<String> levels = new ArrayList<>();
        levels.add("غير محدد");
        for (int i=1;i<=8;i++) levels.add(String.valueOf(i));
        ArrayAdapter<String> levelAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, levels);
        binding.spLevel.setAdapter(levelAdapter);
        binding.spLevel.setSelection(0);
        binding.spLevel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                level = (position==0) ? null : position; // لأن أول عنصر "غير محدد"
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { level = null; }
        });

        // Gender
        ArrayAdapter<String> genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                new String[]{"غير محدد","ذكر","أنثى"});
        binding.spGender.setAdapter(genderAdapter);
        binding.spGender.setSelection(0);
        binding.spGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==1) gender = "male";
                else if (position==2) gender = "female";
                else gender = null;
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { gender = null; }
        });
    }

    // Countries
    private void setupCountries() {
        binding.progressCountries.setVisibility(View.VISIBLE);
        binding.btnRegister.setEnabled(false);

        structRepo.getCountries(new ApiCallback<List<Country>>() {
            @Override public void onSuccess(List<Country> data) {
                countries.clear();
                if (data != null) countries.addAll(data);

                List<String> names = new ArrayList<>();
                names.add("اختر الدولة *");
                for (Country c : countries) names.add(c.name);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, names);
                binding.spCountry.setAdapter(adapter);
                binding.spCountry.setSelection(0);

                binding.spCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) { countryId = null; return; }
                        Country c = countries.get(position - 1);
                        countryId = (int) c.id;
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) { countryId = null; }
                });

                binding.progressCountries.setVisibility(View.GONE);
                binding.btnRegister.setEnabled(true);
            }
            @Override public void onError(String message) {
                binding.progressCountries.setVisibility(View.GONE);
                binding.btnRegister.setEnabled(true);
                toast("تعذر تحميل الدول: " + message);
            }
        });
    }

    // Universities
    private void setupUniversities() {
        binding.progressUniversities.setVisibility(View.VISIBLE);
        structRepo.getUniversities(new ApiCallback<List<University>>() {
            @Override public void onSuccess(List<University> data) {
                universities.clear();
                if (data != null) universities.addAll(data);

                List<String> names = new ArrayList<>();
                names.add("غير محدد");
                for (University u : universities) names.add(u.name);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, names);
                binding.spUniversity.setAdapter(adapter);
                binding.spUniversity.setSelection(0);

                binding.spUniversity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position == 0) {
                            universityId = null;
                            // يعاد ضبط التابعين
                            resetColleges();
                            resetMajors();
                            return;
                        }
                        University u = universities.get(position - 1);
                        universityId = (int) u.id;
                        // جلب الكليات
                        fetchColleges(u.id);
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) {
                        universityId = null;
                        resetColleges();
                        resetMajors();
                    }
                });

                binding.progressUniversities.setVisibility(View.GONE);
            }
            @Override public void onError(String message) {
                binding.progressUniversities.setVisibility(View.GONE);
                toast("تعذر تحميل الجامعات: " + message);
            }
        });
    }

    private void setupColleges() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, new String[]{"غير محدد"});
        binding.spCollege.setAdapter(adapter);
        binding.spCollege.setSelection(0);
        binding.spCollege.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    collegeId = null;
                    resetMajors();
                    return;
                }
                College c = colleges.get(position - 1);
                collegeId = (int) c.id;
                fetchMajors(c.id);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {
                collegeId = null;
                resetMajors();
            }
        });
    }

    private void setupMajors() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, new String[]{"غير محدد"});
        binding.spMajor.setAdapter(adapter);
        binding.spMajor.setSelection(0);
        binding.spMajor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) { majorId = null; return; }
                Major m = majors.get(position - 1);
                majorId = (int) m.id;
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { majorId = null; }
        });
    }

    private void fetchColleges(long uniId) {
        binding.progressColleges.setVisibility(View.VISIBLE);
        structRepo.getColleges(uniId, new ApiCallback<List<College>>() {
            @Override public void onSuccess(List<College> data) {
                colleges.clear();
                if (data != null) colleges.addAll(data);

                List<String> names = new ArrayList<>();
                names.add("غير محدد");
                for (College c : colleges) names.add(c.name);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, names);
                binding.spCollege.setAdapter(adapter);
                binding.spCollege.setSelection(0);

                binding.progressColleges.setVisibility(View.GONE);
            }
            @Override public void onError(String message) {
                binding.progressColleges.setVisibility(View.GONE);
                toast("تعذر تحميل الكليات: " + message);
            }
        });
    }

    private void fetchMajors(long colId) {
        binding.progressMajors.setVisibility(View.VISIBLE);
        structRepo.getMajors(colId, new ApiCallback<List<Major>>() {
            @Override public void onSuccess(List<Major> data) {
                majors.clear();
                if (data != null) majors.addAll(data);

                List<String> names = new ArrayList<>();
                names.add("غير محدد");
                for (Major m : majors) names.add(m.name);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(RegisterActivity.this,
                        android.R.layout.simple_spinner_dropdown_item, names);
                binding.spMajor.setAdapter(adapter);
                binding.spMajor.setSelection(0);

                binding.progressMajors.setVisibility(View.GONE);
            }
            @Override public void onError(String message) {
                binding.progressMajors.setVisibility(View.GONE);
                toast("تعذر تحميل التخصصات: " + message);
            }
        });
    }

    private void resetColleges() {
        colleges.clear();
        collegeId = null;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, new String[]{"غير محدد"});
        binding.spCollege.setAdapter(adapter);
        binding.spCollege.setSelection(0);
    }

    private void resetMajors() {
        majors.clear();
        majorId = null;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, new String[]{"غير محدد"});
        binding.spMajor.setAdapter(adapter);
        binding.spMajor.setSelection(0);
    }

    private void doRegister() {
        String name = val(binding.etName);
        String email = val(binding.etEmail);
        String p1 = val(binding.etPassword);
        String p2 = val(binding.etPassword2);

        if (name.isEmpty() || email.isEmpty() || p1.isEmpty() || p2.isEmpty()) {
            toast("أكمل الحقول المطلوبة");
            return;
        }
        if (!p1.equals(p2)) {
            toast("كلمتا المرور غير متطابقتين");
            return;
        }
        if (countryId == null) {
            toast("اختر الدولة");
            return;
        }

        binding.btnRegister.setEnabled(false);

        authRepo.register(
                name,
                email,
                p1,
                p2,
                "android-" + android.os.Build.VERSION.SDK_INT + "-" + android.os.Build.MODEL,
                countryId,
                universityId,
                collegeId,
                majorId,
                level,
                gender,
                new com.eyadalalimi.students.repo.ApiCallback<TokenResponse>() {
                    @Override public void onSuccess(TokenResponse data) {
                        toast("تم إنشاء الحساب. تحقق البريد مطلوب.");
                        startActivity(new Intent(RegisterActivity.this, VerifyEmailActivity.class));
                        finish();
                    }
                    @Override public void onError(String message) {
                        binding.btnRegister.setEnabled(true);
                        toast(message);
                    }
                }
        );
    }

    private String val(com.google.android.material.textfield.TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_LONG).show(); }
}
