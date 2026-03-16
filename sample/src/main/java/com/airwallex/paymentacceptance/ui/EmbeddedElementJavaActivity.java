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
import com.airwallex.paymentacceptance.R;
import com.airwallex.paymentacceptance.databinding.ActivityEmbeddedElementBinding;

/**
 * Java example of using Embedded Element integration with PaymentElement.
 * <p>
 * This activity demonstrates how to:
 * - Create a PaymentElement in Java using the create + renderInView pattern
 * - Handle payment results with PaymentFlowListener
 * - Embed payment UI in your own activity
 * <p>
 * This is a reference implementation showing how to use the Kotlin-based
 * PaymentElement API from Java code with the two-step pattern:
 * 1. PaymentElement.create() - Creates the element
 * 2. PaymentElement.renderInView() - Renders it in a ComposeView
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

        // Get layout type from intent (null means card-only mode)
        String layoutTypeString = getIntent().getStringExtra("layoutType");

        // Get showsGooglePayAsPrimaryButton from intent (default to true)
        boolean showsGooglePayAsPrimaryButton = getIntent().getBooleanExtra("showsGooglePayAsPrimaryButton", true);

        // Configure payment element based on layout type
        PaymentElementConfiguration configuration;
        if (layoutTypeString != null) {
            // Payment Sheet mode with specified layout
            PaymentMethodsLayoutType layoutType = PaymentMethodsLayoutType.valueOf(layoutTypeString);
            configuration = new PaymentElementConfiguration.PaymentSheet(
                    layoutType,
                    showsGooglePayAsPrimaryButton
            );
        } else {
            // Card-only mode (no payment sheet)
            configuration = new PaymentElementConfiguration.Card(
                    java.util.Arrays.asList(com.airwallex.android.core.AirwallexSupportedCard.values())
            );
        }

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

        // Show loading
        mBinding.progressBar.setVisibility(android.view.View.VISIBLE);
        mBinding.composeCardInfo.setVisibility(android.view.View.GONE);

        // Create PaymentElement using the create + renderInView pattern
        PaymentElement.create(
                session,                        // AirwallexSession
                airwallex,                      // Airwallex instance
                configuration,                  // PaymentElementConfiguration
                listener,                       // PaymentFlowListener
                new PaymentElementCallback() {
                    @Override
                    public void onSuccess(@NonNull PaymentElement element) {
                        // Hide loading and show content
                        mBinding.progressBar.setVisibility(android.view.View.GONE);
                        mBinding.composeCardInfo.setVisibility(android.view.View.VISIBLE);

                        // Render the PaymentElement in the ComposeView
                        PaymentElement.renderInView(element, mBinding.composeCardInfo);
                    }

                    @Override
                    public void onFailure(@NonNull Throwable error) {
                        // Hide loading
                        mBinding.progressBar.setVisibility(android.view.View.GONE);

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
     *
     * @param context The context
     * @param session The payment session
     * @param layoutType The layout type for payment sheet (null for card-only mode)
     * @param showsGooglePayAsPrimaryButton Whether to show Google Pay as primary button (ignored in card-only mode)
     */
    public static void start(
            Context context,
            AirwallexSession session,
            PaymentMethodsLayoutType layoutType,
            boolean showsGooglePayAsPrimaryButton
    ) {
        Intent intent = new Intent(context, EmbeddedElementJavaActivity.class);
        intent.putExtra("session", session);
        if (layoutType != null) {
            intent.putExtra("layoutType", layoutType.name());
        }
        intent.putExtra("showsGooglePayAsPrimaryButton", showsGooglePayAsPrimaryButton);
        context.startActivity(intent);
    }
}
