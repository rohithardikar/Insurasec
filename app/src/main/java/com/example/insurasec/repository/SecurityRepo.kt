package com.example.insurasec.repository

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SecurityRepo(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) {

    fun encryptAndUpload(name: String, phone: String, medCerLink: String) {
        uploadToFirebase(
            encrypt(name, phone, medCerLink)
        )
    }

    fun getDataFromFirebase() {
        auth.currentUser?.email?.let { it ->
            db.collection("users")
                .document(it)
                .get()
                .addOnSuccessListener {
                    Log.e("SecurityRepo", "Retrieved Data -> ${it.get("name")}")
                    val data = binaryToString(
                        dnaToBinary(it.get("name").toString(), 1)
                    )
                    Toast.makeText(context, "Data -> $data", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun uploadToFirebase(data: String) {
        val cypherText = hashMapOf(
            "name" to data
        )

        auth.currentUser?.email?.let {
            db.collection("users")
                .document(it)
                .set(cypherText)
                .addOnSuccessListener {
                    Toast.makeText(context, "Data Added Successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun encrypt(name: String, phone: String, medCerLink: String): String {
        Log.e("SecurityRepo", "Original Name -> $name")
        val binary = stringToBinary(name)
        Log.e("SecurityRepo", "Binary Name -> $binary")
        val dna = binaryToDna(binary, 1)

        Log.e("SecurityRepo", "DNA Name -> $dna")
        return dna
    }

    private fun stringToBinary(data: String): String {
        val binaryStringBuilder = StringBuilder()

        for (char in data) {
            val binaryValue = Integer.toBinaryString(char.code)
            binaryStringBuilder.append(binaryValue.padStart(8, '0')) // Ensure 8-bit representation
        }

        return binaryStringBuilder.toString()
    }

    private fun binaryToDna(binary: String, encodeMapNumber: Int): String {
        val chunks = binary.chunked(2)
        val resultStringBuilder = StringBuilder()

        for (chunk in chunks) {
            Log.e("SecurityRepo", "Chunk -> $chunk")
            val dnaValue = binaryToDnaMap(chunk, encodeMapNumber)
            Log.e("SecurityRepo", "DNA Value -> $dnaValue , MapNumber -> $encodeMapNumber")
            resultStringBuilder.append(dnaValue)
        }

        return resultStringBuilder.toString()
    }

    private fun dnaToBinary(cypherText: String, decodeMapNumber: Int): String {
        val chunks = cypherText.chunked(1)
        val resultStringBuilder = StringBuilder()

        for (chunk in chunks) {
            Log.e("SecurityRepo", "Chunk -> $chunk")
            val binaryValue = dnaToBinaryMap(chunk, decodeMapNumber)
            Log.e("SecurityRepo", "BinaryValue -> $binaryValue , MapNumber -> $decodeMapNumber")
            resultStringBuilder.append(binaryValue)
        }

        Log.e("SecurityRepo", "BinaryName -> $resultStringBuilder , MapNumber -> $decodeMapNumber")
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
}