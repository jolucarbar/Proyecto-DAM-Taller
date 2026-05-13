
package com.joseluis.apptaller.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Clase de utilidades para la gestión de seguridad y cifrado del ERP.
 * 
 * @author José Luis Cárdenas Barroso
 * @info Proyecto Intermodular del Grado Superior DAM
 * @institution IES Augustóbriga
 */
public class SeguridadUtil {
    private static final int ITERACIONES = 65536;
    private static final int LONGITUD_LLAVE = 256;

    // Crea un hash seguro a partir de una contraseña plana
    public static String generarHash(String password) throws Exception {
        // 1. Generar un "Salt" aleatorio 
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);

        // 2. Crear el hash usando PBKDF2
        byte[] hash = pbkdf2(password.toCharArray(), salt);

        // 3. Guardamos el Salt y el Hash juntos en un solo String (separados por :)
        return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
    }

    // Verifica si la contraseña tecleada coincide con el hash guardado
    public static boolean verificarPassword(String passwordTecleada, String hashGuardado) throws Exception {
        String[] partes = hashGuardado.split(":");
        byte[] salt = Base64.getDecoder().decode(partes[0]);
        byte[] hashReal = Base64.getDecoder().decode(partes[1]);

        byte[] hashPrueba = pbkdf2(passwordTecleada.toCharArray(), salt);

        // Comparación segura tiempo-constante
        int diff = hashReal.length ^ hashPrueba.length;
        for (int i = 0; i < hashReal.length && i < hashPrueba.length; i++) {
            diff |= hashReal[i] ^ hashPrueba[i];
        }
        return diff == 0;
    }
    
    
    
/**
 * Método que ejecuta el algoritmo criptográfico PBKDF2 (Password-Based Key Derivation Function 2).
 * aplica una técnica que repite un algoritmo matemático miles de veces para
 * hacer que los ataques por fuerza bruta sean casi imposibles de realizar.
 * @param password
 * @param salt
 * @return
 * @throws NoSuchAlgorithmException
 * @throws InvalidKeySpecException 
 */
    private static byte[] pbkdf2(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        // "Con este password, aplícale el algoritmo (salt) un número de veces (ITERACONES) y 
        // dame un resultado de tamaño LONGITUD_LLAVE."
        PBEKeySpec spec = new PBEKeySpec(password, salt, ITERACIONES, LONGITUD_LLAVE);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return skf.generateSecret(spec).getEncoded();
    }
}
