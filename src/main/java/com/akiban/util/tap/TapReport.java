/**
 * END USER LICENSE AGREEMENT (“EULA”)
 *
 * READ THIS AGREEMENT CAREFULLY (date: 9/13/2011):
 * http://www.akiban.com/licensing/20110913
 *
 * BY INSTALLING OR USING ALL OR ANY PORTION OF THE SOFTWARE, YOU ARE ACCEPTING
 * ALL OF THE TERMS AND CONDITIONS OF THIS AGREEMENT. YOU AGREE THAT THIS
 * AGREEMENT IS ENFORCEABLE LIKE ANY WRITTEN AGREEMENT SIGNED BY YOU.
 *
 * IF YOU HAVE PAID A LICENSE FEE FOR USE OF THE SOFTWARE AND DO NOT AGREE TO
 * THESE TERMS, YOU MAY RETURN THE SOFTWARE FOR A FULL REFUND PROVIDED YOU (A) DO
 * NOT USE THE SOFTWARE AND (B) RETURN THE SOFTWARE WITHIN THIRTY (30) DAYS OF
 * YOUR INITIAL PURCHASE.
 *
 * IF YOU WISH TO USE THE SOFTWARE AS AN EMPLOYEE, CONTRACTOR, OR AGENT OF A
 * CORPORATION, PARTNERSHIP OR SIMILAR ENTITY, THEN YOU MUST BE AUTHORIZED TO SIGN
 * FOR AND BIND THE ENTITY IN ORDER TO ACCEPT THE TERMS OF THIS AGREEMENT. THE
 * LICENSES GRANTED UNDER THIS AGREEMENT ARE EXPRESSLY CONDITIONED UPON ACCEPTANCE
 * BY SUCH AUTHORIZED PERSONNEL.
 *
 * IF YOU HAVE ENTERED INTO A SEPARATE WRITTEN LICENSE AGREEMENT WITH AKIBAN FOR
 * USE OF THE SOFTWARE, THE TERMS AND CONDITIONS OF SUCH OTHER AGREEMENT SHALL
 * PREVAIL OVER ANY CONFLICTING TERMS OR CONDITIONS IN THIS AGREEMENT.
 */

package com.akiban.util.tap;

import java.beans.ConstructorProperties;

public class TapReport
{
    @Override
    public String toString()
    {
        return String.format("%s: in = %s, out = %s, msec = %s", name, inCount, outCount, cumulativeTime / MILLION);
    }

    @ConstructorProperties({"name", "inCount", "outCount", "cumulativeTime"})
    public TapReport(String name, long inCount, long outCount, long cumulativeTime)
    {
        this.name = name;
        this.inCount = inCount;
        this.outCount = outCount;
        this.cumulativeTime = cumulativeTime;
    }
    
    public TapReport(String name)
    {
        this.name = name;
        this.inCount = 0;
        this.outCount = 0;
        this.cumulativeTime = 0;
    }

    public String getName()
    {
        return name;
    }

    public long getInCount()
    {
        return inCount;
    }

    public long getOutCount()
    {
        return outCount;
    }

    public long getCumulativeTime()
    {
        return cumulativeTime;
    }

    // Class state
    
    private static final int MILLION = 1000000;
    
    // Object state

    String name;
    long inCount;
    long outCount;
    long cumulativeTime;
}