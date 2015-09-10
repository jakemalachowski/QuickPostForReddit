package com.malachowski.quickpostforreddit.Utils;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by Jacob on 7/15/2015.
 */
public final class SessionIdentifierGenerator
{
    private SecureRandom random = new SecureRandom();

    public String nextSessionId()
    {
        return new BigInteger(30, random).toString(32);
    }
}