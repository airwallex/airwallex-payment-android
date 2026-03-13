package com.airwallex.paymentacceptance.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.airwallex.android.core.Airwallex;
import com.airwallex.android.core.AirwallexPaymentStatus;
import com.airwallex.android.core.AirwallexSession;
import com.airwallex.android.core.PaymentMethodsLayoutType;
import com.airwallex.android.ui.AirwallexLoadingDialogFragment;
import com.airwallex.android.view.PaymentFlowListener;
import com.airwallex.android.view.composables.PaymentElement;
import com.airwallex.android.view.composables.PaymentElementCallback;
import com.airwallex.android.view.composables.PaymentElementConfiguration;
import com.airwallex.android.view.composables.PaymentElementHelper;
import com.airwallex.paymentacceptance.R;
import com.airwallex.paymentacceptance.databinding.ActivityEmbeddedElementBinding;

/**
 * Java example of using Embedded Element integration with PaymentElement.
 * <p>
 * This activity demonstrates how to:
 * - Create a PaymentElement in Java
 * - Handle payment results with PaymentFlowListener
 * - Embed payment UI in your own activity
 * <p>
 * This is a reference implementation showing how to use the Kotlin-based
 * PaymentElement API from Java code.
 */
public class EmbeddedElementJavaActivity extends AppCompatActivity {

    private ActivityEmbeddedElementBinding mBinding;
    private Airwallex airwallex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityEmbeddedElementBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        airwallex = new Airwallex(this);
        setupUI();
        setupPaymentElement();
    }

    private void setupUI() {
        mBinding.btnBack.setOnClickListener(v -> finish());
        mBinding.tvTitle.setText("Java Embedded Element Example");
    }

    private void setupPaymentElement() {
        // Get session from intent (in real app, you'd create this from your backend)
        Parcelable parcelable = getIntent().getParcelableExtra("session");
        if (!(parcelable instanceof AirwallexSession)) {
            showError("Session is required");
            return;
        }
        AirwallexSession session = (AirwallexSession) parcelable;

        // Configure payment element - using Payment Sheet with Tab layout
        PaymentElementConfiguration configuration = new PaymentElementConfiguration.PaymentSheet(
                PaymentMethodsLayoutType.TAB,
                true  // showsGooglePayAsPrimaryButton
        );

        // Create payment flow listener
        PaymentFlowListener listener = new PaymentFlowListener() {
            @Override
            public void onLoadingStateChanged(boolean isLoading, @NonNull Context context) {
                // Handle loading state changes during payment using loading dialog
                runOnUiThread(() -> {
                    if (isLoading) {
                        AirwallexLoadingDialogFragment.Companion.show(EmbeddedElementJavaActivity.this);
                    } else {
                        AirwallexLoadingDialogFragment.Companion.hide(EmbeddedElementJavaActivity.this);
                    }
                });
            }

            @Override
            public void onPaymentResult(@NonNull AirwallexPaymentStatus status) {
                // Handle payment result
                if (status instanceof AirwallexPaymentStatus.Success) {
                    AirwallexPaymentStatus.Success success = (AirwallexPaymentStatus.Success) status;
                    showSuccess(success.getPaymentIntentId());
                } else if (status instanceof AirwallexPaymentStatus.Failure) {
                    AirwallexPaymentStatus.Failure failure = (AirwallexPaymentStatus.Failure) status;
                    showError(failure.getException().getMessage());
                } else if (status instanceof AirwallexPaymentStatus.Cancel) {
                    showCancelled();
                } else if (status instanceof AirwallexPaymentStatus.InProgress) {
                    // Payment in progress (loading handled by onLoadingStateChanged)
                }
            }

            @Override
            public void onError(@NonNull Throwable exception, @NonNull Context context) {
                // Optional: Handle errors during element initialization or payment
                showError(exception.getMessage());
            }
        };

        // Create and render PaymentElement using the Java-friendly helper
        PaymentElementHelper.create(
                this,                           // ComponentActivity
                session,                        // AirwallexSession
                airwallex,                      // Airwallex instance
                configuration,                  // PaymentElementConfiguration
                listener,                       // PaymentFlowListener
                mBinding.composeCardInfo,       // ComposeView for rendering
                mBinding.progressBar,           // ProgressBar (optional, for loading state)
                new PaymentElementCallback() {
                    @Override
                    public void onSuccess(@NonNull PaymentElement element) {
                        // PaymentElement created successfully and already rendered
                        // No additional action needed - the UI is already displayed
                    }

                    @Override
                    public void onFailure(@NonNull Throwable error) {
                        // Handle creation error
                        showError(error.getMessage() != null ? error.getMessage() : "Failed to create payment element");
                    }
                }
        );
    }

    private void showSuccess(String paymentIntentId) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.payment_successful)
                .setMessage(getString(R.string.payment_successful_message))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    setResult(RESULT_OK);
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.payment_failed)
                .setMessage(message != null ? message : getString(R.string.payment_failed_message))
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }

    private void showCancelled() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.payment_cancelled)
                .setMessage(R.string.payment_cancelled_message)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    setResult(RESULT_CANCELED);
                    finish();
                })
                .show();
    }

    /**
     * Launch EmbeddedElementJavaActivity with a session
     */
    public static void start(Context context, AirwallexSession session) {
        Intent intent = new Intent(context, EmbeddedElementJavaActivity.class);
        intent.putExtra("session", session);
        context.startActivity(intent);
    }
}
