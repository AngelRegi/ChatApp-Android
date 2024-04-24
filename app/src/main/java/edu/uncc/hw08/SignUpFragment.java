package edu.uncc.hw08;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import edu.uncc.hw08.databinding.FragmentSignUpBinding;

public class SignUpFragment extends Fragment {
    private FirebaseAuth mAuth;
    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentSignUpBinding binding;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.gotoLogin();
            }
        });


        binding.buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.editTextEmail.getText().toString();
                String password = binding.editTextPassword.getText().toString();
                String name = binding.editTextName.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(getContext(), "Email is required", Toast.LENGTH_SHORT).show();
                } else if (password.isEmpty()) {
                    Toast.makeText(getContext(), "Password is required", Toast.LENGTH_SHORT).show();
                } else if (name.isEmpty()) {
                    Toast.makeText(getContext(), "Name is required", Toast.LENGTH_SHORT).show();
                } else {
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();
                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            HashMap<String, Object> user = new HashMap<>();
                                            user.put("name", name);
                                            user.put("uid", mAuth.getCurrentUser().getUid());
                                            user.put("email", email);
                                            user.put("isLoggedIn", false);
                                            db.collection("users").add(user)
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.d("demo", "onSuccess: user imported");
                                                        }
                                                    });
                                            mListener.gotoMyChat();
                                        } else {
                                            Toast.makeText(getContext(), "Error in creating user", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            } else {
                                Toast.makeText(getContext(), "Sign up failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }
            }
        });

        getActivity().setTitle("Sign Up");

    }

    SignUpListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (SignUpListener) context;
    }

    interface SignUpListener {
        void gotoMyChat();
        void gotoLogin();
    }
}