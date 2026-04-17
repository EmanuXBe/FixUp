package edu.javeriana.fixup.di

import edu.javeriana.fixup.data.network.api.FixUpApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * NetworkModule es un módulo de Hilt encargado de proveer las dependencias de red.
 * Se instala en SingletonComponent para asegurar que las instancias de Retrofit y OkHttpClient
 * sean únicas durante todo el ciclo de vida de la aplicación, optimizando el uso de recursos.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * URL Base del backend. 
     * Para la defensa académica: Se utiliza HTTPS para garantizar la cifrado de datos en tránsito.
     * En desarrollo local se suele usar "http://10.0.2.2:8080/", pero para producción en Render 
     * se requiere la URL pública con protocolo seguro.
     */
    private const val BASE_URL = "https://fixup-backend-erh9.onrender.com/"

    /**
     * Provee una instancia configurada de OkHttpClient.
     * Justificación Técnica:
     * 1. Cold Starts en Render: Al usar el plan gratuito, el servidor entra en hibernación. 
     *    Se incrementan los timeouts a 60 segundos para evitar que la app aborte la conexión 
     *    mientras el servidor se "despierta".
     * 2. Seguridad: OkHttpClient maneja automáticamente el handshake de TLS para HTTPS.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // Interceptor de logging para depuración en desarrollo (opcional pero recomendado)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            // Tiempo máximo para establecer la conexión inicial con el servidor
            .connectTimeout(60, TimeUnit.SECONDS)
            // Tiempo máximo de espera entre paquetes de datos una vez establecida la conexión
            .readTimeout(60, TimeUnit.SECONDS)
            // Tiempo máximo para enviar datos al servidor
            .writeTimeout(60, TimeUnit.SECONDS)
            // Reintento automático en caso de fallos de conexión recuperables
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Provee la instancia de Retrofit.
     * Arquitectura: Se inyecta el OkHttpClient configurado previamente. 
     * Esto separa la configuración del transporte (HTTP) de la definición de la interfaz de la API.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Cliente HTTP personalizado con los timeouts extendidos
            .client(okHttpClient)
            // Conversor de JSON a objetos Kotlin (DTOs)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provee la implementación de la interfaz FixUpApiService creada por Retrofit.
     * Esta es la dependencia que inyectarán los Data Sources.
     */
    @Provides
    @Singleton
    fun provideFixUpApiService(retrofit: Retrofit): FixUpApiService {
        return retrofit.create(FixUpApiService::class.java)
    }
}
