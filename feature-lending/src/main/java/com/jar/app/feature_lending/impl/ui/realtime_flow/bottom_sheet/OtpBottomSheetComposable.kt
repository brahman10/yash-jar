import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.text.toSpannable
import com.jar.app.core_compose_ui.component.JarPrimaryButton
import com.jar.app.core_compose_ui.theme.jarFontFamily
import com.jar.app.core_compose_ui.utils.toAnnotatedString
import com.jar.app.core_ui.R


@Composable
fun EnterOtpBottomSheet(
    modifier: Modifier = Modifier,
    subTitle: Spannable,
    onSubmitClick: (String) -> Unit,
    onBackCrossClicked: () -> Unit,
    onResendOtpClick: () -> Unit,
    otpTimer: Int
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colorResource(id = R.color.bgColor))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(95.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Image(
                modifier = Modifier
                    .padding(top = 20.dp, end = 16.dp)
                    .clickable { onBackCrossClicked() },
                painter = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_bottom_sheet_cross_icon),
                contentDescription = "cross"
            )
        }
        Image(
            modifier = Modifier.padding(start = 16.dp, bottom = 12.dp),
            painter = painterResource(id = com.jar.app.feature_lending.R.drawable.feature_lending_ic_otp),
            contentDescription = "cross"
        )
        Text(
            modifier = Modifier.padding(start = 16.dp),
            text = stringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_verify_bank_with_otp.resourceId),
            style = TextStyle(
                fontSize = 24.sp,
                lineHeight = 32.sp,
                fontFamily = jarFontFamily,
                fontWeight = FontWeight(700),
                color = Color.White,
            )
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 134.dp),
            text = subTitle.toAnnotatedString(),
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 21.sp,
                fontFamily = jarFontFamily,
                fontWeight = FontWeight(400),
                color = colorResource(id = com.jar.app.core_ui.R.color.color_D5CDF2),
            )
        )
        var otpValue by remember {
            mutableStateOf("")
        }

        OtpTextField(otpText = otpValue, onOtpTextChange = { value, otpInputFilled ->
            otpValue = value

        })

        val spannableStringOtpText = SpannableStringBuilder()
        val normalText =
            SpannableString(stringResource(com.jar.app.feature_lending.shared.MR.strings.featiure_lending_resend_otp.resourceId))
        spannableStringOtpText.append(normalText)
        val boldText = SpannableStringBuilder(" 0:$otpTimer")
        boldText.setSpan(
            ForegroundColorSpan(android.graphics.Color.WHITE),
            0,
            boldText.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannableStringOtpText.append( boldText)


        Text(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.End)
                .padding(top = 16.dp, end = 16.dp)
                .clickable(enabled = otpTimer != 0, onClick = onResendOtpClick),
            text = spannableStringOtpText.toAnnotatedString(),
            style = TextStyle(
                fontSize = 14.sp,
                lineHeight = 21.sp,
                fontFamily = jarFontFamily,
                fontWeight = FontWeight(400),
                color = colorResource(id = com.jar.app.core_ui.R.color.smallTxtColor),
                textAlign = TextAlign.End,
            )
        )

        JarPrimaryButton(modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 51.dp),
            text = stringResource(com.jar.app.feature_lending.shared.MR.strings.feature_lending_submit.resourceId),
            onClick = { onSubmitClick(otpValue) })


    }

}


@Composable
fun OtpTextField(
    modifier: Modifier = Modifier.fillMaxWidth(),
    otpText: String,
    otpCount: Int = 6,
    onOtpTextChange: (String, Boolean) -> Unit
) {
    LaunchedEffect(Unit) {
        if (otpText.length > otpCount) {
            throw IllegalArgumentException("Otp text value must not have more than otpCount: $otpCount characters")
        }
    }

    BasicTextField(
        modifier = modifier,
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        onValueChange = {
            if (it.text.length <= otpCount) {
                onOtpTextChange.invoke(it.text, it.text.length == otpCount)

            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.Center) {
                repeat(otpCount) { index ->
                    CharView(
                        index = index,
                        text = otpText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    )
}

@Composable
private fun CharView(
    index: Int,
    text: String
) {
    Column(Modifier.padding(start = 6.5.dp, end = 6.5.dp)) {
        val isFocused = text.length == index
        val char = when {
            index == text.length -> ""
            index > text.length -> ""
            else -> text[index].toString()
        }
        Text(
            modifier = Modifier
                .width(44.dp)
                .padding(2.dp),
            text = char,
            fontFamily = jarFontFamily,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        Divider(
            Modifier
                .width(44.dp)
                .padding(bottom = 2.dp, start = 2.dp),
            color = if (isFocused) colorResource(id = R.color.white) else colorResource(id = R.color.color_776e94),
            thickness = 1.dp
        )

    }

}

@Preview
@Composable
fun Preview() {
    val text = "Enter the OTP sent by CAMS to your mobile \nnumber +91 XXXXX-93748 "

    EnterOtpBottomSheet(
        subTitle = text.toSpannable(),
        onSubmitClick = ::gg,
        onBackCrossClicked = {},
        onResendOtpClick = {},
        otpTimer = 20,
    )
}

private fun gg(aa: String) {


}