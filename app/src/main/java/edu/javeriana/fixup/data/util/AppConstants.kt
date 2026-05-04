package edu.javeriana.fixup.data.util

object AppConstants {
    const val CURRENT_USER_ID = "1"
    const val CURRENT_USER_ID_INT = 1

    /**
     * Host para el emulador de Firebase.
     * Se usa "10.0.2.2" porque es la dirección IP especial que permite al emulador de Android
     * acceder al "localhost" de la máquina anfitriona donde se ejecutan los emuladores de Firebase.
     */
    const val EMULATOR_HOST = "10.0.2.2"

    /**
     * Puertos estándar configurados en el Firebase Emulator Suite.
     * El uso de emuladores garantiza que las pruebas de desarrollo no afecten
     * ni "ensucien" la base de datos de producción.
     */
    const val AUTH_PORT = 9099
    const val FIRESTORE_PORT = 8080
    const val STORAGE_PORT = 9199
}
