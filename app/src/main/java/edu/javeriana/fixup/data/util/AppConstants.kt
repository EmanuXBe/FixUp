package edu.javeriana.fixup.data.util

object AppConstants {
    const val CURRENT_USER_ID = "1"
    const val CURRENT_USER_ID_INT = 1

    /**
     * Host para el emulador de Firebase.
     *
     * Se usa "127.0.0.1" junto con `adb reverse` para que tanto dispositivos físicos
     * como emuladores AVD puedan alcanzar el emulador de Firebase en la máquina anfitriona.
     *
     * Antes de correr los tests instrumentados, ejecutar en terminal:
     *   adb reverse tcp:8080 tcp:8080   (Firestore)
     *   adb reverse tcp:9099 tcp:9099   (Auth)
     *   adb reverse tcp:9199 tcp:9199   (Storage)
     *
     * Y asegurarse de que el emulador esté corriendo:
     *   firebase emulators:start
     *
     * Los servicios son visibles en el Dashboard: http://localhost:4000
     */
    const val EMULATOR_HOST = "10.0.2.2"

    /**
     * Puertos estándar configurados en el Firebase Emulator Suite.
     * Estos corresponden a los servicios listados en el Hub (puerto 4000).
     */
    const val AUTH_PORT = 9099
    const val FIRESTORE_PORT = 8080
    const val STORAGE_PORT = 9199
}
