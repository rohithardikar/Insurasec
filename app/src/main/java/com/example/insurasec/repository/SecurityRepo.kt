package com.example.insurasec.repository

import android.content.Context
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream

class SecurityRepo(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    fun encryptAndUpload(
        name: String,
        phone: String,
        ageProof: String,
        addressProof: String,
        identityProof: String,
        medCerLink: String
    ) {
        val compressedName = Base64.encodeToString(compressString(name), Base64.DEFAULT)
        val compressedPhone = Base64.encodeToString(compressString(phone), Base64.DEFAULT)
        val compressedAgeProof = Base64.encodeToString(compressString(ageProof), Base64.DEFAULT)
        val compressedAddressProof = Base64.encodeToString(compressString(addressProof), Base64.DEFAULT)
        val compressedIdentityProof = Base64.encodeToString(compressString(identityProof), Base64.DEFAULT)
        val compressedMedicalCertificate = Base64.encodeToString(compressString(medCerLink), Base64.DEFAULT)
        Log.e("SecurityRepo", "Compressed Name: $compressedName\nCompressed Phone: $compressedPhone")

        if (
            name.isBlank() ||
            phone.isBlank() ||
            ageProof.isBlank() ||
            addressProof.isBlank() ||
            identityProof.isBlank() ||
            medCerLink.isBlank() ||
            !phone.isDigitsOnly() ||
            phone.length != 10
            ) {
            Toast.makeText(context, "Invalid Input", Toast.LENGTH_SHORT).show()
            return
        }

        encrypt(
            compressedName,
            compressedPhone,
            compressedAgeProof,
            compressedAddressProof,
            compressedIdentityProof,
            compressedMedicalCertificate
        ).let {
            uploadToFirebase(it[0], it[1])
        }
    }

    fun getDataFromFirebase() {
        auth.currentUser?.email?.let { it ->
            db.collection("users")
                .document(it)
                .collection("insurance_info")
                .document("insurance_data")
                .get()
                .addOnSuccessListener {
                    val dna = arrayListOf(
                        it.get("name").toString(),
                        it.get("phone").toString(),
                        it.get("age_proof").toString(),
                        it.get("address_proof").toString(),
                        it.get("identity_proof").toString(),
                        it.get("medical_certificate").toString()
                    )
                    val randomMapList = arrayListOf(
                        it.get("name_random_map").toString(),
                        it.get("phone_random_map").toString(),
                        it.get("age_proof_random_map").toString(),
                        it.get("address_proof_random_map").toString(),
                        it.get("identity_proof_random_map").toString(),
                        it.get("medical_certificate_random_map").toString()
                    )
                    Log.e("SecurityRepo", "DnaList: $dna\nRandomMapList: $randomMapList")
                    val compressedData = decrypt(dna, randomMapList)
                    val name = decompressString(Base64.decode(compressedData[0], Base64.DEFAULT))
                    val phone = decompressString(Base64.decode(compressedData[1], Base64.DEFAULT))
                    val ageProof = decompressString(Base64.decode(compressedData[2], Base64.DEFAULT))
                    val addressProof = decompressString(Base64.decode(compressedData[3], Base64.DEFAULT))
                    val identityProof = decompressString(Base64.decode(compressedData[4], Base64.DEFAULT))
                    val medicalCertificate = decompressString(Base64.decode(compressedData[5], Base64.DEFAULT))

                    Toast.makeText(context, "Name: $name", Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "Phone: $phone", Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "AgeProof: $ageProof", Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "AddressProof: $addressProof", Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "IdentityProof: $identityProof", Toast.LENGTH_SHORT).show()
                    Toast.makeText(context, "MedicalCertificate: $medicalCertificate", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadToFirebase(dna: ArrayList<String>, randomMapList: ArrayList<String>) {
        val cypherText = hashMapOf(
            "name" to dna[0],
            "name_random_map" to randomMapList[0],
            "phone" to dna[1],
            "phone_random_map" to randomMapList[1],
            "age_proof" to dna[2],
            "age_proof_random_map" to randomMapList[2],
            "address_proof" to dna[3],
            "address_proof_random_map" to randomMapList[3],
            "identity_proof" to dna[4],
            "identity_proof_random_map" to randomMapList[4],
            "medical_certificate" to dna[5],
            "medical_certificate_random_map" to randomMapList[5]
        )

        auth.currentUser?.email?.let {
            db.collection("users")
                .document(it)
                .collection("insurance_info")
                .document("insurance_data")
                .set(cypherText)
                .addOnSuccessListener {
                    Toast.makeText(context, "Data Added Successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun decrypt(dna: ArrayList<String>, randomMapList: ArrayList<String>): ArrayList<String> {
        return arrayListOf(
            binaryToString(dnaToBinary(dna[0], randomMapList[0])),
            binaryToString(dnaToBinary(dna[1], randomMapList[1])),
            binaryToString(dnaToBinary(dna[2], randomMapList[2])),
            binaryToString(dnaToBinary(dna[3], randomMapList[3])),
            binaryToString(dnaToBinary(dna[4], randomMapList[4])),
            binaryToString(dnaToBinary(dna[5], randomMapList[5]))
        )
    }

    private fun encrypt(
        name: String,
        phone: String,
        ageProof: String,
        addressProof: String,
        identityProof: String,
        medCerLink: String
    ): ArrayList<ArrayList<String>> {
        val binaryName = stringToBinary(name)
        val binaryPhone = stringToBinary(phone)
        val binaryAgeProof = stringToBinary(ageProof)
        val binaryAddressProof = stringToBinary(addressProof)
        val binaryIdentityProof = stringToBinary(identityProof)
        val binaryMedicalCertificate = stringToBinary(medCerLink)
        Log.e("SecurityRepo", "Binary Name: $binaryName\nBinary Phone: $binaryPhone")

        val dna = arrayListOf<String>()
        val randomMapList = arrayListOf<String>()
        binaryToDna(binaryName).let {
            dna.add(it[0])
            randomMapList.add(it[1])
        }
        binaryToDna(binaryPhone).let {
            dna.add(it[0])
            randomMapList.add(it[1])
        }
        binaryToDna(binaryAgeProof).let {
            dna.add(it[0])
            randomMapList.add(it[1])
        }
        binaryToDna(binaryAddressProof).let {
            dna.add(it[0])
            randomMapList.add(it[1])
        }
        binaryToDna(binaryIdentityProof).let {
            dna.add(it[0])
            randomMapList.add(it[1])
        }
        binaryToDna(binaryMedicalCertificate).let {
            dna.add(it[0])
            randomMapList.add(it[1])
        }

        Log.e("SecurityRepo", "DNA Name: ${dna[0]}, Random Map List: ${randomMapList[0]}")
        return arrayListOf(
            dna,
            randomMapList
        )
    }

    private fun stringToBinary(data: String): String {
        val binaryStringBuilder = StringBuilder()

        for (char in data) {
            val binaryValue = Integer.toBinaryString(char.code)
            binaryStringBuilder.append(binaryValue.padStart(8, '0')) // Ensure 8-bit representation
        }

        return binaryStringBuilder.toString()
    }

    private fun binaryToDna(binary: String): ArrayList<String> {
        val chunks = binary.chunked(2)
        val resultStringBuilder = StringBuilder()
        val randomMapListStringBuilder = StringBuilder()

        for (chunk in chunks) {
            val randomMapNumber = randomMapNumber()
            Log.e("SecurityRepo", "Chunk -> $chunk")
            val dnaValue = binaryToDnaMap(chunk, randomMapNumber)
            Log.e("SecurityRepo", "DNA Value -> $dnaValue , MapNumber -> $randomMapNumber")
            resultStringBuilder.append(dnaValue)
            randomMapListStringBuilder.append(randomMapNumber)
        }

        return arrayListOf(
            resultStringBuilder.toString(),
            randomMapListStringBuilder.toString()
        )
    }

    private fun dnaToBinary(cypherText: String, randomMapList: String): String {
        val chunks = cypherText.chunked(1)
        val resultStringBuilder = StringBuilder()

        for ((i, chunk) in chunks.withIndex()) {
            val randomMap = randomMapList[i].digitToInt()
            Log.e("SecurityRepo", "Chunk -> $chunk")
            val binaryValue = dnaToBinaryMap(chunk, randomMap)
            Log.e("SecurityRepo", "BinaryValue -> $binaryValue , MapNumber -> $randomMap")
            resultStringBuilder.append(binaryValue)
        }

        Log.e("SecurityRepo", "BinaryName -> $resultStringBuilder , MapNumber -> $randomMapList")
        return resultStringBuilder.toString()
    }

    private fun binaryToString(binary: String): String {
        val chunks = binary.chunked(8)
        val resultStringBuilder = StringBuilder()

        for (chunk in chunks) {
            val decimalValue = Integer.parseInt(chunk, 2)
            resultStringBuilder.append(decimalValue.toChar())
        }

        return resultStringBuilder.toString()
    }

    private fun binaryToDnaMap(binary: String, mapNumber: Int): String {
        Log.e("SecurityRepo", "Binary -> $binary , MapNumber -> $mapNumber")
        return when (mapNumber) {
            1 -> when (binary) {
                "00" -> "A"
                "01" -> "T"
                "10" -> "C"
                "11" -> "G"
                else -> ""
            }
            2 -> when (binary) {
                "00" -> "G"
                "01" -> "A"
                "10" -> "T"
                "11" -> "C"
                else -> ""
            }
            3 -> when (binary) {
                "00" -> "C"
                "01" -> "G"
                "10" -> "A"
                "11" -> "T"
                else -> ""
            }
            4 -> when (binary) {
                "00" -> "T"
                "01" -> "C"
                "10" -> "G"
                "11" -> "A"
                else -> ""
            }
            else -> return ""
        }
    }

    private fun dnaToBinaryMap(cypherText: String, mapNumber: Int): String {
        Log.e("SecurityRepo", "CypherText -> $cypherText , MapNumber -> $mapNumber")
        return when (mapNumber) {
            1 -> when (cypherText) {
                "A" -> "00"
                "T" -> "01"
                "C" -> "10"
                "G" -> "11"
                else -> ""
            }
            2 -> when (cypherText) {
                "A" -> "01"
                "T" -> "10"
                "C" -> "11"
                "G" -> "00"
                else -> ""
            }
            3 -> when (cypherText) {
                "A" -> "10"
                "T" -> "11"
                "C" -> "00"
                "G" -> "01"
                else -> ""
            }
            4 -> when (cypherText) {
                "A" -> "11"
                "T" -> "00"
                "C" -> "01"
                "G" -> "10"
                else -> ""
            }
            else -> return ""
        }
    }

    private fun randomMapNumber(): Int {
        return (1..4).random()
    }

    private fun compressString(input: String): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        try {
            GZIPOutputStream(byteArrayOutputStream).bufferedWriter(StandardCharsets.UTF_8).use { it.write(input) }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return byteArrayOutputStream.toByteArray()
    }

    private fun decompressString(input: ByteArray): String {
        val byteArrayInputStream = ByteArrayInputStream(input)
        val stringBuilder = StringBuilder()

        try {
            GZIPInputStream(byteArrayInputStream).bufferedReader(StandardCharsets.UTF_8).useLines { lines ->
                lines.forEach { stringBuilder.append(it) }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return stringBuilder.toString()
    }
}