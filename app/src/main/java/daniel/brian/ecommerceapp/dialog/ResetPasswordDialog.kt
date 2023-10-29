package daniel.brian.ecommerceapp.dialog

import android.annotation.SuppressLint
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import daniel.brian.ecommerceapp.R

// This is an extension function for setting up a custom bottomSheetFragment.It means that it can be called on any fragment
@SuppressLint("InflateParams")
fun Fragment.setupBottomSheet(
    // It is called when the user clicks send on the bottom sheet
    onSendClick: (String) -> Unit,
) {
    // instance of the BottomSheet
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.reset_password_dialog, null)
    dialog.setContentView(view)
    // Prevents the keyboard from hiding the buttons for send and cancel
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    // making the dialog visible to the user
    dialog.show()

    // Accessing the required components using their IDs
    val emailTxt = view.findViewById<EditText>(R.id.resetPwd)
    val buttonSend = view.findViewById<Button>(R.id.resetBtnSend)
    val buttonCancel = view.findViewById<Button>(R.id.resetBtnCancel)

    buttonSend.setOnClickListener {
        // it extracts the text trims it and then call the sendOnClick with it
        val email = emailTxt.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }

    buttonCancel.setOnClickListener {
        dialog.dismiss()
    }
}
