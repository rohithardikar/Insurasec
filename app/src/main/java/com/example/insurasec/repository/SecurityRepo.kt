package com.example.insurasec.repository

import android.content.Context
import android.util.Base64
import android.util.Log
import android.widget.Toast
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

    fun encryptAndUpload(name: String, phone: String, medCerLink: String) {
        var dna: String
        var randomMapList: String

        val compressedName = Base64.encodeToString(compressString(name), Base64.DEFAULT)
        Log.e("SecRepo", "StringComName: $compressedName")
//        val compressedPhone = compressString(phone).toString()
//        val compressedMedCerLink = compressString(medCerLink).toString()
        encrypt(compressedName, phone, medCerLink).let {
            dna = it[0]
            randomMapList = it[1]
        }

        uploadToFirebase(dna, randomMapList)
    }

    fun getDataFromFirebase() {
        auth.currentUser?.email?.let { it ->
            db.collection("users")
                .document(it)
                .collection("insurance_info")
                .document("insurance_data")
                .get()
                .addOnSuccessListener {
                    val compressedData = decrypt(
                        it.get("name").toString(),
                        it.get("random_map_list").toString()
                    )

                    val data = decompressString(Base64.decode(compressedData, Base64.DEFAULT))

                    Toast.makeText(context, "Data -> $data", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadToFirebase(data: String, randomMapList: String) {
        val cypherText = hashMapOf(
            "name" to data,
            "random_map_list" to randomMapList
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

    private fun decrypt(dna: String, randomMapList: String): String {
        Log.e("SecurityRepo", "Retrieved Data -> $dna")

        return binaryToString(
            dnaToBinary(dna, randomMapList)
        )
    }

    private fun encrypt(name: String, phone: String, medCerLink: String): ArrayList<String> {
        Log.e("SecurityRepo", "Original Name -> $name")
        val binary = stringToBinary(name)
        Log.e("SecurityRepo", "Binary Name -> $binary")
        var dna: String
        var randomMapList: String
        binaryToDna(binary).let {
            dna = it[0]
            randomMapList = it[1]
        }

        Log.e("SecurityRepo", "DNA Name -> $dna")
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