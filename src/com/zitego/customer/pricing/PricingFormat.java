package com.zitego.customer.pricing;

import java.text.Format;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;

/**
 * A format for a pricing object. It takes a standard decimal format in the constructor
 * then formats the object based on if it is a Number or a Pricing object. If it is a Number
 * then a DecimalFormat class formats it. If it is a pricing object, then it is fomatted depending
 * on the type.
 *
 * @author John Glorioso
 * @version $Id: PricingFormat.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class PricingFormat extends Format
{
    private DecimalFormat _decFmt;
    private String _format;

	/**
	 * Creates a new PricingFormat with the decimal string pattern.
	 *
	 * @param fmt The format pattern string for prices.
	 */
	public PricingFormat(String fmt)
	{
		_decFmt = new DecimalFormat(fmt);
		_format = fmt;
	}

	/**
	 * Formats the given object.
	 *
	 * @param obj The object to format.
	 * @param toAppendTo What to append the formatted value to.
	 * @param pos Not used.
	 * @return String
	 */
	public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition notUsed)
	{
	    StringBuffer ret = toAppendTo;
		if (obj instanceof Number)
		{
			ret = _decFmt.format(obj, toAppendTo, notUsed);
		}
		else if (obj instanceof Pricing)
		{
		    Pricing pricing = (Pricing)obj;
			if (pricing.getType() == PricingType.REGULAR_PRICE || pricing.getType() == PricingType.SALE_PRICE)
			{
			    ret = _decFmt.format(new Float(pricing.getPrice(null)), toAppendTo, notUsed);
			}
			else if (pricing.getType() == PricingType.OPTION_BASED)
			{
			    ret.append("Depends on Selected Options");
			}
			else if (pricing.getType() == PricingType.VOLUME_BASED)
			{
			    ret.append( ((VolumeBased)pricing).getConfig().getEnglishInterpretation(_format) );
			}
		}
		return ret;
	}

	/**
	 * Does nothing. This method is not implemented. Null is always returned.
	 *
	 * @param source Not used.
	 * @param pos Not used.
	 * @return Object
	 */
	public Object parseObject(String source, ParsePosition pos)
	{
		return null;
	}
}