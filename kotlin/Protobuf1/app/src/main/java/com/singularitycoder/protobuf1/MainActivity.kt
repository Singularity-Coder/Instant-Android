package com.singularitycoder.protobuf1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

// References
// https://proandroiddev.com/how-to-setup-your-android-app-to-use-protobuf-96132340de5c
// https://medium.com/mobile-app-development-publication/simple-android-protobuf-tutorial-with-actual-code-bfb581299f47
// https://programming.vip/docs/5d7defbf14314.html
// https://proandroiddev.com/protobuf-in-android-55b01d855c40
class MainActivity : AppCompatActivity() {

    companion object {
        private const val SAMPLE_JSON =
                """{
  "addressbook": [
    {
      "person": {
        "id": 1,
        "name": "Singularity Coder",
        "email": "hithesh@singularitycoder.com"
      }
    },
    {
      "phones": [
        {
          "phone": {
            "number": "+91999999999",
            "type": "HOME"
          }
        },
        {
          "phone": {
            "number": "+91000000000",
            "type": "MOBILE"
          }
        }
      ]
    }
  ]
}"""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val addressBook = buildAddressBookProtoObject()

        // Serialize AddressBook to ByteArray
        val bytes = addressBook.toByteArray()

        // Deserialize AddressBook ByteArray
        val myAddressBook = AddressBookProtos.AddressBook.parseFrom(bytes)

        println("Address Book: ${myAddressBook.peopleOrBuilderList[0].email}")
        println("Protobuf byte size: ${bytes.size}")
        println("JSON byte size: ${SAMPLE_JSON.toByteArray().size}")
    }

    private fun buildAddressBookProtoObject(): AddressBookProtos.AddressBook {
        // Building PhoneNumber objects
        val phoneHome = AddressBookProtos.Person.PhoneNumber.newBuilder()
                .setNumber("+91999999999")
                .setType(AddressBookProtos.Person.PhoneType.HOME)
                .build()
        val phoneMobile = AddressBookProtos.Person.PhoneNumber.newBuilder()
                .setNumber("+91000000000")
                .setType(AddressBookProtos.Person.PhoneType.MOBILE)
                .build()

        // Building a Person object using phone data
        val person = AddressBookProtos.Person.newBuilder()
                .setId(1)
                .setEmail("hithesh@singularitycoder.com")
                .setName("Singularity Coder")
                .addAllPhones(listOf<AddressBookProtos.Person.PhoneNumber>(phoneHome, phoneMobile))
                .build()

        // Building an AddressBook object using person data
        return AddressBookProtos.AddressBook.newBuilder()
                .addAllPeople(listOf(person))
                .build()
    }
}