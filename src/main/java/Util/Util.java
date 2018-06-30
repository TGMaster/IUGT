/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Util;

import java.security.SecureRandom;
import org.apache.commons.lang3.RandomStringUtils;

/**
 *
 * @author TGMaster
 */
public class Util {

    public static String generateRandomStr(int length) {
        char[] possibleCharacters = ("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789").toCharArray();
        String randomStr = RandomStringUtils.random(length, 0, possibleCharacters.length - 1, false, false, possibleCharacters, new SecureRandom());

        return randomStr;
    }
}
