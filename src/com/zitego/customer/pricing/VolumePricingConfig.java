package com.zitego.customer.pricing;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * This class represents a volume pricing configuration. The configuration string
 * is in the format of: qty1:price1;qty2-qty5:price2;qty6:price3.<br>
 * For example: 1:4.95;2-5:4.50;6:4.25<br>
 * This means a single item is $4.95, 2-5 are $4.50 each, and 6 and up are $4.25
 * each.
 *
 * @author John Glorioso
 * @version $Id: VolumePricingConfig.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class VolumePricingConfig
{
    private Vector _ranges;

    public static void main(String[] args) throws Exception
    {
        if (args.length != 2)
        {
            System.out.println("Usage: java PricingConfig <volume config string> <price format>");
            System.exit(1);
        }
        VolumePricingConfig cfg = new VolumePricingConfig(args[0]);
        System.out.println( "toString = "+cfg.toString() );
        System.out.println( "english = "+cfg.getEnglishInterpretation(args[1]) );
    }

    /**
     * Creates a new VolumePricingConfig from the given config string. If
     * the string is invalid, then a ParseException is thrown.
     *
     * @param config The config string.
     */
    public VolumePricingConfig(String config) throws ParseException
    {
        if (config == null) throw new ParseException("Invalid volume pricing config string: "+config, 0);
        StringTokenizer st = new StringTokenizer(config, ";");
        _ranges = new Vector();
        while ( st.hasMoreTokens() )
        {
            String token = st.nextToken();
            int index = token.indexOf(":");
            if (index == -1) throw new ParseException("Invalid volume pricing config string: "+config, 0);
            String sRange = token.substring(0, index);
            int low = -1;
            int high = -1;
            float price = -1f;
            try
            {
                price = Float.parseFloat( token.substring(index+1) );
            }
            catch (NumberFormatException nfe)
            {
                throw new ParseException("Invalid volume pricing config string: "+config, 0);
            }
            index = sRange.indexOf("-");
            if (index == -1)
            {
                try
                {
                    low = Integer.parseInt(sRange);
                    if ( st.hasMoreTokens() ) high = low;
                }
                catch (NumberFormatException nfe)
                {
                    throw new ParseException("Invalid volume pricing config string: "+config, 0);
                }
            }
            else
            {
                try
                {
                    low = Integer.parseInt( sRange.substring(0, index) );
                    high = Integer.parseInt( sRange.substring(index+1) );
                }
                catch (NumberFormatException nfe)
                {
                    throw new ParseException("Invalid volume pricing config string: "+config, 0);
                }
            }
            _ranges.add( new PriceRange(low, high, price) );
        }
    }

    /**
     * Returns the price for the given quantity.
     *
     * @param qty The quantity.
     * @return float
     */
    public float getPrice(int qty)
    {
        int size = _ranges.size();
        for (int i=0; i<size; i++)
        {
            PriceRange range = (PriceRange)_ranges.get(i);
            if ( qty >= range.low && (qty <= range.high || range.high == -1) )
            {
                return range.price;
            }
        }
        return 0;
    }

    /**
     * Returns the string representation of this configuration.
     *
     * @return String
     */
    public String toString()
    {
        int size = _ranges.size();
        StringBuffer ret = new StringBuffer();
        for (int i=0; i<size; i++)
        {
            PriceRange range = (PriceRange)_ranges.get(i);
            ret.append( (i > 0 ? ";" : "") ).append(range.low);
            if (range.low != range.high && range.high > range.low) ret.append("-").append(range.high);
            ret.append(":").append(range.price);
        }
        return ret.toString();
    }

    /**
     * Returns the english representation of this configuration. Ex: 1 to 3/each $5.00, 4 $4.50/each, over 5 $4.00/each
     *
     * @param fmt The decimal format to use for prices.
     * @return String
     */
    public String getEnglishInterpretation(String fmt)
    {
        int size = _ranges.size();
        StringBuffer ret = new StringBuffer();
        DecimalFormat f = new DecimalFormat(fmt);
        for (int i=0; i<size; i++)
        {
            PriceRange range = (PriceRange)_ranges.get(i);
            ret.append( (i > 0 ? ", " : "") );
            if (range.low == range.high) ret.append(range.low);
            else if (range.high > range.low) ret.append(range.low).append(" to ").append(range.high);
            else ret.append(" over ").append(range.low);
            ret.append(" ").append( f.format(new Float(range.price)) ).append("/each");
        }
        return ret.toString();
    }

    private class PriceRange
    {
        private int low = 0;
        private int high = 0;
        private float price = 0f;

        private PriceRange(int low, int high, float price)
        {
            this.low = low;
            this.high = high;
            this.price = price;
        }
    }
}