package com.example.asvcd_riskcalculator

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.exp
import android.util.Log

class MainActivity : AppCompatActivity() {

    // Enum for Race
    enum class Race {
        WHITE, BLACK, OTHER
    }

    // Enum for Gender
    enum class Gender {
        MALE, FEMALE
    }

    // Enum for Blood Pressure Medication Status
    enum class BpMedStatus {
        YES, NO
    }

    // Enum for Diabetes Status
    enum class DiabetesStatus {
        YES, NO
    }

    // Enum for Smoking Status
    enum class SmokingStatus {
        YES, NO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val calculateButton: Button = findViewById(R.id.buttonCalculate)
        val ascvdRiskTextView: TextView = findViewById(R.id.textViewASCVDRisk)

        calculateButton.setOnClickListener {
            val ageEditText: EditText = findViewById(R.id.editTextAge)
            val age: Int = ageEditText.text.toString().toInt()

            val systolicBpEditText: EditText = findViewById(R.id.editTextSystolicBP)
            val sbp: Int = systolicBpEditText.text.toString().toInt()

            val cholesterolEditText: EditText = findViewById(R.id.editTextTotalCholesterol)
            val cholesterol: Int = cholesterolEditText.text.toString().toInt()

            val hdlEditText: EditText = findViewById(R.id.editTextHDLCholesterol)
            val hdl: Int = hdlEditText.text.toString().toInt()

            val biologicalSexGroup: RadioGroup = findViewById(R.id.radioGroupBiologicalSex)
            val biologicalSex: Gender = when (biologicalSexGroup.checkedRadioButtonId) {
                R.id.radioButtonMale -> Gender.MALE
                R.id.radioButtonFemale -> Gender.FEMALE
                else -> Gender.MALE
            }

            val blackGroup: RadioGroup = findViewById(R.id.radioGroupBlack)
            val isBlack: Race = when (blackGroup.checkedRadioButtonId) {
                R.id.radioButtonAfricanAmerican -> Race.BLACK
                R.id.radioButtonWhite -> Race.WHITE
                else -> Race.OTHER
            }
            val isSmokingGroup: RadioGroup = findViewById(R.id.radioGroupSmokingTobacco)
            val isSmoking: SmokingStatus = when (isSmokingGroup.checkedRadioButtonId) {
                R.id.radioButtonSmokingYes -> SmokingStatus.YES
                R.id.radioButtonSmokingNo -> SmokingStatus.NO
                else -> SmokingStatus.NO
            }

            val hasDiabetesGroup: RadioGroup = findViewById(R.id.radioGroupDiabetes)
            val hasDiabetes: DiabetesStatus = when (hasDiabetesGroup.checkedRadioButtonId) {
                R.id.radioButtonDiabetesYes -> DiabetesStatus.YES
                R.id.radioButtonDiabetesNo -> DiabetesStatus.NO
                else -> DiabetesStatus.NO
            }

            val radioGroupBloodPressure: RadioGroup = findViewById(R.id.radioGroupBloodPressure)
            val bloodPressureMedication: BpMedStatus = when (radioGroupBloodPressure.checkedRadioButtonId) {
                R.id.radioButtonBPYes -> BpMedStatus.YES
                R.id.radioButtonBPNo -> BpMedStatus.NO
                else -> BpMedStatus.NO
            }

            val totalCholesterolEditText: EditText = findViewById(R.id.editTextTotalCholesterol)
            val totalCholesterol: Int = totalCholesterolEditText.text.toString().toInt()

            val hdlCholesterolEditText: EditText = findViewById(R.id.editTextHDLCholesterol)
            val hdlCholesterol: Int = hdlCholesterolEditText.text.toString().toInt()

            val systolicBloodPressureEditText: EditText = findViewById(R.id.editTextSystolicBP)
            val systolicBloodPressure: Int = systolicBloodPressureEditText.text.toString().toInt()

            val ascvdRisk: Double = calculateValue( isBlack, biologicalSex, age, sbp,
                bloodPressureMedication, hasDiabetes, isSmoking, cholesterol, hdl)

            ascvdRiskTextView.text = "10-year ASCVD risk: ${"%.2f".format(ascvdRisk)}%"
        }
    }

    // Function to get the intercept coefficient based on race and gender
    private fun getInterceptCoefficient(race: Race, gender: Gender): Double {
        return when (race to gender) {
            Race.WHITE to Gender.MALE -> -11.67998
            Race.WHITE to Gender.FEMALE -> -12.82311
            Race.BLACK to Gender.MALE -> -11.67998
            Race.BLACK to Gender.FEMALE -> -12.82311
            Race.OTHER to Gender.MALE -> -11.67998
            Race.OTHER to Gender.FEMALE -> -12.82311
            else -> throw IllegalArgumentException("Invalid combination of race and gender")
        }
    }

    // Function to get the age coefficient based on race and gender
    private fun getAgeCoefficient(race: Race, gender: Gender): Double {
        return when (race to gender) {
            Race.WHITE to Gender.MALE -> 0.064200
            Race.WHITE to Gender.FEMALE -> 0.106501
            Race.BLACK to Gender.MALE -> 0.064200
            Race.BLACK to Gender.FEMALE -> 0.106501
            Race.OTHER to Gender.MALE -> 0.064200
            Race.OTHER to Gender.FEMALE -> 0.106501
            else -> throw IllegalArgumentException("Invalid combination of race and gender")
        }
    }

    // Function to get the black race coefficient based on race and gender
    private fun getBlackRaceCoefficient(race: Race, gender: Gender): Double {
        return when (race to gender) {
            Race.WHITE to Gender.MALE -> 0.000000
            Race.WHITE to Gender.FEMALE -> 0.000000
            Race.BLACK to Gender.MALE -> 0.482835
            Race.BLACK to Gender.FEMALE -> 0.432440
            Race.OTHER to Gender.MALE -> 0.000000
            Race.OTHER to Gender.FEMALE -> 0.000000
            else -> throw IllegalArgumentException("Invalid combination of race and gender")
        }
    }

    // Function to get the SBP coefficient based on race and gender
    fun getSbpCoefficient(race: Race, gender: Gender): Double {
        return when (race to gender) {
            Race.WHITE to Gender.MALE -> 0.038950
            Race.WHITE to Gender.FEMALE -> 0.017666
            Race.BLACK to Gender.MALE -> 0.038950
            Race.BLACK to Gender.FEMALE -> 0.017666
            Race.OTHER to Gender.MALE -> 0.038950
            Race.OTHER to Gender.FEMALE -> 0.017666
            else -> throw IllegalArgumentException("Invalid combination of race and gender")
        }
    }

    // Function to get the SBP squared coefficient based on race and gender
    private fun getSbpSquaredCoefficient(race: Race, gender: Gender): Double {
        return when (race to gender) {
            Race.WHITE to Gender.MALE -> -0.000061
            Race.WHITE to Gender.FEMALE -> 0.000056
            Race.BLACK to Gender.MALE -> -0.000061
            Race.BLACK to Gender.FEMALE -> 0.000056
            Race.OTHER to Gender.MALE -> 0.000061
            Race.OTHER to Gender.FEMALE -> 0.000056
            else -> throw IllegalArgumentException("Invalid combination of race and gender")
        }
    }

    // Function to get the BP medication coefficient based on race, gender, and BP med status
    private fun getBpMedCoefficient(race: Race, gender: Gender, bpMedStatus: BpMedStatus): Double {
        if (bpMedStatus == BpMedStatus.NO) {
            return 0.0
        }
        return when (race to gender) {
            Race.WHITE to Gender.MALE -> 2.055533
            Race.WHITE to Gender.FEMALE -> 0.731678
            Race.BLACK to Gender.MALE -> 2.055533
            Race.BLACK to Gender.FEMALE -> 0.731678
            Race.OTHER to Gender.MALE -> 2.055533
            Race.OTHER to Gender.FEMALE -> 0.731678
            else -> throw IllegalArgumentException("Invalid combination of race and gender")
        }
    }

    // Function to get the diabetes coefficient based on race, gender, and diabetes status
    private fun getDiabetesCoefficient(race: Race, gender: Gender, diabetesStatus: DiabetesStatus): Double {
        if (diabetesStatus == DiabetesStatus.NO) {
            return 0.0
        }
        return when (race to gender) {
            Race.WHITE to Gender.MALE -> 0.842209
            Race.WHITE to Gender.FEMALE -> 0.943970
            Race.BLACK to Gender.MALE -> 0.842209
            Race.BLACK to Gender.FEMALE -> 0.943970
            Race.OTHER to Gender.MALE -> 0.842209
            Race.OTHER to Gender.FEMALE -> 0.943970
            else -> throw IllegalArgumentException("Invalid combination of race and gender")
        }
    }

    // Function to get the smoking coefficient based on race, gender, and smoking status
    private fun getSmokingCoefficient(race: Race, gender: Gender, smokingStatus: SmokingStatus): Double {
        if (smokingStatus == SmokingStatus.NO) {
            return 0.0
        }
        return when (race to gender) {
            Race.WHITE to Gender.MALE -> 0.895589
            Race.WHITE to Gender.FEMALE -> 1.009790
            Race.BLACK to Gender.MALE -> 0.895589
            Race.BLACK to Gender.FEMALE -> 1.009790
            Race.OTHER to Gender.MALE -> 0.895589
            Race.OTHER to Gender.FEMALE -> 1.009790
            else -> throw IllegalArgumentException("Invalid combination of race and gender")
        }
    }

    // Function to get the Chol total-to-HDL ratio coefficient based on race and gender
    private fun getCholToHDLCoefficient(race: Race, gender: Gender): Double {
        return when (race to gender) {
            Race.WHITE to Gender.MALE -> 0.193307
            Race.WHITE to Gender.FEMALE -> 0.151318
            Race.BLACK to Gender.MALE -> 0.193307
            Race.BLACK to Gender.FEMALE -> 0.151318
            Race.OTHER to Gender.MALE -> 0.193307
            Race.OTHER to Gender.FEMALE -> 0.151318
            else -> throw IllegalArgumentException("Invalid combination of race and gender")
        }
    }

    // Function to get the Age if Black coefficient based on gender
    private fun getAgeIfBlackCoefficient(gender: Gender): Double {
        return when (gender) {
            Gender.MALE -> 0.0
            Gender.FEMALE -> -0.008580
        }
    }

    // Function to get the Systolic BP if taking BP med coefficient based on gender and race
    private fun getSystolicBpIfOnMedCoefficient(race: Race, gender: Gender): Double {
        return when (race) {
            Race.WHITE -> when (gender) {
                Gender.MALE -> -0.014207
                Gender.FEMALE -> -0.003647
            }
            Race.BLACK -> when (gender) {
                Gender.MALE -> -0.014207
                Gender.FEMALE -> -0.003647
            }
            Race.OTHER -> when(gender){
                Gender.MALE -> -0.014207
                Gender.FEMALE -> -0.003647
            }
        }
    }

    // Function to get the Systolic BP if Black coefficient based on gender
    private fun getSystolicBpIfBlackCoefficient(gender: Gender): Double {
        return when (gender) {
            Gender.MALE -> 0.011609
            Gender.FEMALE -> 0.006208
        }
    }

    // Function to get the Black Race & Taking BP Med coefficient based on race, gender, and BP med status
    private fun getBlackRaceAndBpMedCoefficient(race: Race, gender: Gender, bpMedStatus: BpMedStatus): Double {
        return when (race) {
            Race.OTHER -> 0.0
            Race.WHITE -> 0.0
            Race.BLACK -> {
                when (gender) {
                    Gender.MALE -> if (bpMedStatus == BpMedStatus.YES) -0.119460 else 0.0
                    Gender.FEMALE -> if (bpMedStatus == BpMedStatus.YES) 0.152968 else 0.0
                }
            }
        }
    }

    // Function to get the Age x Systolic BP coefficient based on race and gender
    private fun getAgeSbpCoefficient(race: Race, gender: Gender): Double {
        return when (race) {
            Race.WHITE -> {
                when (gender) {
                    Gender.MALE -> 0.000025
                    Gender.FEMALE -> -0.000153
                }
            }
            Race.BLACK -> {
                when (gender) {
                    Gender.MALE -> 0.000025
                    Gender.FEMALE -> -0.000153
                }
            }
            Race.OTHER -> {
                when (gender){
                    Gender.MALE -> 0.000025
                    Gender.FEMALE -> -0.000153
                }
            }
        }
    }

    //  Function to get the Smoker if Black coefficient based on race, gender, and smoking status
    private fun getSmokerIfBlackCoefficient(race: Race, gender: Gender, smokingStatus: SmokingStatus): Double {
        if (race == Race.BLACK && smokingStatus == SmokingStatus.YES) {
            return when (gender) {
                Gender.MALE -> -0.226771
                Gender.FEMALE -> -0.092231
            }
        }
        return 0.0
    }

    // Function to get the Diabetes if Black coefficient based on race and gender
    private fun getDiabetesIfBlackCoefficient(race: Race, gender: Gender, diabetesStatus: DiabetesStatus): Double {
        if (race == Race.BLACK && diabetesStatus == DiabetesStatus.YES) {
            return when (gender) {
                Gender.MALE -> -0.077214
                Gender.FEMALE -> 0.115232
            }
        }
        return 0.0
    }

    // Function to get the Chol total-to-HDL ratio if Black coefficient based on race and gender
    private fun getCholToHDLRatioIfBlackCoefficient(race: Race, gender: Gender): Double {
        if (race == Race.BLACK) {
            return when (gender) {
                Gender.MALE -> -0.117749
                Gender.FEMALE -> 0.070498
            }
        }
        return 0.0
    }

    // Function to get the Systolic BP if on BP med if Black coefficient based on race and gender
    fun getSystolicBpIfOnMedIfBlackCoefficient(race: Race, gender: Gender): Double {
        if (race == Race.BLACK) {
            return when (gender) {
                Gender.MALE -> 0.004190
                Gender.FEMALE -> -0.000173
            }
        }
        return 0.0
    }


    // Function to get the Age x Systolic BP if Black coefficient based on race and gender
    private fun getAgeSbpIfBlackCoefficient(race: Race, gender: Gender): Double {
        if (race == Race.BLACK) {
            return when (gender) {
                Gender.MALE -> -0.000199
                Gender.FEMALE -> -0.000094
            }
        }
        return 0.0
    }

    fun calculateValue(
        race: Race, gender: Gender, age: Int, sbp: Int,
        bpMedStatus: BpMedStatus, diabetesStatus: DiabetesStatus,
        smokingStatus: SmokingStatus, chol: Int, hdl: Int
    ): Double {

        // Get the respective coefficients
        val interceptCoefficient = getInterceptCoefficient(race, gender)
        Log.d("AppLog", "interceptCoefficient: $interceptCoefficient")

        val ageCoefficient = getAgeCoefficient(race, gender)*age
        Log.d("AppLog", "ageCoefficient: $ageCoefficient")

        val blackRaceCoefficient = getBlackRaceCoefficient(race, gender)
        Log.d("AppLog", "blackRaceCoefficient: $blackRaceCoefficient")

        val sbpSquaredCoefficient = getSbpSquaredCoefficient(race, gender)
        Log.d("AppLog", "sbpSquaredCoefficient: $sbpSquaredCoefficient")

        val sbpCoefficient = getSbpCoefficient(race, gender)*sbp
        Log.d("AppLog", "sbpCoefficient: $sbpCoefficient")

        val bpMedCoefficient = getBpMedCoefficient(race, gender, bpMedStatus)
        Log.d("AppLog", "bpMedCoefficient: $bpMedCoefficient")

        val diabetesCoefficient = getDiabetesCoefficient(race, gender, diabetesStatus)
        Log.d("AppLog", "diabetesCoefficient: $diabetesCoefficient")

        val smokingCoefficient = getSmokingCoefficient(race, gender, smokingStatus)
        Log.d("AppLog", "smokingCoefficient: $smokingCoefficient")

        // Calculate pt-values for SBP and SBP squared
        val sbpSquaredValue = sbp.toDouble() * sbp.toDouble() * sbpSquaredCoefficient
        val sbpValue = sbp.toDouble() * sbpCoefficient
        Log.d("AppLog", "Calculate pt-values for SBP and SBP squared: $sbpSquaredValue")

        // Computing the pt-value for Chol total-to-HDL ratio
        val cholToHDLValue = chol / hdl
        Log.d("AppLog", "Computing the pt-value for Chol total-to-HDL ratio: $cholToHDLValue")

        // Get the Chol total-to-HDL ratio coefficient and compute the product with pt-value
        val cholToHDLCoefficient = getCholToHDLCoefficient(race, gender)
        val cholToHDLProduct = cholToHDLValue * cholToHDLCoefficient
        Log.d("AppLog", "Get the Chol total-to-HDL ratio coefficient and compute the product with pt-value: $cholToHDLProduct")

        // Computing the pt-value for Age if Black
        val ageIfBlackValue = if (race == Race.BLACK) age.toDouble() else 0.0
        Log.d("AppLog", "Computing the pt-value for Age if Black: $ageIfBlackValue")

        // Get the Age if Black coefficient and compute the product with pt-value
        val ageIfBlackCoefficient = getAgeIfBlackCoefficient(gender)
        val ageIfBlackProduct = ageIfBlackValue * ageIfBlackCoefficient
        Log.d("AppLog", "Get the Age if Black coefficient and compute the product with pt-value: $ageIfBlackProduct")

        // Computing the pt-value for Systolic BP if taking BP med
        val systolicBpIfOnMedValue = if (bpMedStatus == BpMedStatus.YES) sbp.toDouble() else 0.0
        Log.d("AppLog", "Computing the pt-value for Systolic BP if taking BP med: $systolicBpIfOnMedValue")

        // Get the Systolic BP if taking BP med coefficient and compute the product with pt-value
        val systolicBpIfOnMedCoefficient = getSystolicBpIfOnMedCoefficient(race, gender)
        Log.d("AppLog", "Get the Systolic BP if taking BP med coefficient systolicBpIfOnMedCoefficient: $systolicBpIfOnMedCoefficient")

        val systolicBpIfOnMedProduct = systolicBpIfOnMedValue * systolicBpIfOnMedCoefficient
        Log.d("AppLog", "Get the Systolic BP if taking BP med coefficient and compute the product with pt-value: $systolicBpIfOnMedProduct")

        // Computing the pt-value for Systolic BP if Black
        val systolicBpIfBlackValue = if (race == Race.BLACK) sbp.toDouble() else 0.0
        Log.d("AppLog", "Computing the pt-value for Systolic BP if Black: $systolicBpIfBlackValue")

        // Get the Systolic BP if Black coefficient and compute the product with pt-value
        val systolicBpIfBlackCoefficient = getSystolicBpIfBlackCoefficient(gender)
        val systolicBpIfBlackProduct = systolicBpIfBlackValue * systolicBpIfBlackCoefficient
        Log.d("AppLog", "Get the Systolic BP if Black coefficient and compute the product with pt-value: $systolicBpIfBlackProduct")

        // Get the Black Race & Taking BP Med coefficient
        val blackRaceAndBpMedCoefficient =
            getBlackRaceAndBpMedCoefficient(race, gender, bpMedStatus)
        Log.d("AppLog", "Get the Black Race & Taking BP Med coefficient: $blackRaceAndBpMedCoefficient")

        // Calculate the Age x Systolic BP product
        val ageSbpProduct = age * sbp.toDouble()
        Log.d("AppLog", "Calculate the Age x Systolic BP product: $ageSbpProduct")

        // Get the Age x Systolic BP coefficient
        val ageSbpCoefficient = getAgeSbpCoefficient(race, gender)
        Log.d("AppLog", "Get the Age x Systolic BP coefficient: $ageSbpCoefficient")

        // Calculate the value for the Age x Systolic BP factor
        val ageSbpValue = ageSbpProduct * ageSbpCoefficient
        Log.d("AppLog", "Calculate the value for the Age x Systolic BP factor: $ageSbpValue")

        // Calculate the Diabetes if Black value
        val diabetesIfBlackValue = getDiabetesIfBlackCoefficient(race, gender, diabetesStatus)
        Log.d("AppLog", "Calculate the Diabetes if Black value: $diabetesIfBlackValue")

        // Calculate the Smoker if Black value
        val smokerIfBlackValue = getSmokerIfBlackCoefficient(race, gender, smokingStatus)
        Log.d("AppLog", "Calculate the Smoker if Black value: $smokerIfBlackValue")

        // Calculate the Chol total-to-HDL ratio if Black value
        val cholToHDLIfBlackCoefficient = getCholToHDLRatioIfBlackCoefficient(race, gender)
        val cholToHDLIfBlackValue = cholToHDLIfBlackCoefficient * (chol / hdl)
        Log.d("AppLog", "Calculate the Chol total-to-HDL ratio if Black value: $cholToHDLIfBlackValue")

        // Calculate the Systolic BP if on BP med if Black value, only if the user is on BP medication
        val systolicBpIfOnMedIfBlackCoefficient = if (bpMedStatus == BpMedStatus.YES) {
            getSystolicBpIfOnMedIfBlackCoefficient(race, gender)
        } else {
            0.0
        }
        val systolicBpIfOnMedIfBlackValue = systolicBpIfOnMedIfBlackCoefficient * sbp
        Log.d("AppLog", "Calculate the Systolic BP if on BP med if Black value, only if the user is on BP medication: $systolicBpIfOnMedIfBlackValue")

        // Calculate the Age x Systolic BP if black value
        val ageSbpIfBlackCoefficient = getAgeSbpIfBlackCoefficient(race, gender)
        val ageSbpIfBlackValue = ageSbpIfBlackCoefficient * age * sbp
        Log.d("AppLog", "Calculate the Age x Systolic BP if black value: $ageSbpIfBlackValue")

        // Calculating the sum of all the terms
        val totalSum: Double = interceptCoefficient + ageCoefficient + blackRaceCoefficient + bpMedCoefficient +
                    diabetesCoefficient + smokingCoefficient + sbpSquaredValue + sbpCoefficient + cholToHDLProduct +
                    ageIfBlackProduct + systolicBpIfOnMedProduct + systolicBpIfBlackProduct +
                    blackRaceAndBpMedCoefficient + ageSbpValue + diabetesIfBlackValue +
                    smokerIfBlackValue + cholToHDLIfBlackValue + systolicBpIfOnMedIfBlackValue +
                    ageSbpIfBlackValue
        Log.d("AppLog", "Calculating the sum of the terms: $totalSum")

        // Calculating the negative sum of the terms
        val negTotalSum: Double = 0 - totalSum
        Log.d("AppLog", "Calculating the negative sum of the terms: $negTotalSum")

        // Calculating the exponent of negative sum of terms
        val expNegSum: Double = exp(negTotalSum)
        Log.d("AppLog", "Calculating the exponent of negative sum of terms: $expNegSum")

        // Calculating the exponent of negative sum of terms plus 1
        val expNegSumPlusOne: Double = expNegSum + 1
        Log.d("AppLog", "Calculating the exponent of negative sum of terms plus 1: $expNegSumPlusOne")

        // Calculating the risk percentage term
        val riskTerm: Double = (1 / expNegSumPlusOne) * 100
        Log.d("AppLog", "Calculating the risk percentage term: $riskTerm")

        //Returning the percentage of the risk term
        return riskTerm.coerceIn(0.0, 100.0)
        Log.d("AppLog", "Returning the percentage of the risk term: $riskTerm")

    }

    }
