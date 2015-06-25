package com.zitego.customer.delivery;

import com.zitego.customer.product.Product;
import com.zitego.markup.xml.XmlTag;
import com.zitego.format.FormatType;
import com.zitego.util.NameValueObject;
import java.util.Vector;

/**
 * A class for returning rates for a product based on product attributes.
 *
 * @author John Glorioso
 * @version $Id: ProductBased.java,v 1.1.1.1 2008/02/20 15:07:59 jglorioso Exp $
 */
public class ProductBased extends DeliveryMethod
{
    /**
     * Creates a new product based rates delivery method with config information.
     *
     * @param config The properties of the delivery method.
     */
    public ProductBased(String config)
    {
        //Try to parse the config as xml
        try
        {
            XmlTag xml = new XmlTag();
            xml.parse(config, FormatType.XML);
            Vector children = xml.getChildrenWithName("product");
            int size = children.size();
            Vector options = new Vector(size);
            for (int i=0; i<size; i++)
            {
                XmlTag child = (XmlTag)children.get(i);
                options.add( new NameValueObject(child.getTagAttribute("price"), child.getTagAttribute("label")) );
            }
            NameValueObject[] opts = new NameValueObject[size];
            options.copyInto(opts);
            setOptions(opts);
        }
        catch (Exception e) {e.printStackTrace();}
    }

    public float getShippingCharges(Product p)
    {
        NameValueObject obj = getSelectedOption();
        if (obj != null) return Float.parseFloat( obj.getValue() );
        else return 0;
    }

    public DeliveryMethodType getType()
    {
        return DeliveryMethodType.PRODUCT_BASED;
    }

    public String getConfig()
    {
        StringBuffer config = new StringBuffer();
        NameValueObject[] opts = getOptions();
        if (opts == null) opts = new NameValueObject[0];
        if (opts.length > 0) config.append("<config>");
        for (int i=0; i<opts.length; i++)
        {
            config.append("<product label=\"").append( opts[i].getName() ).append("\" price=\"").append( opts[i].getValue() ).append("\" />");
        }
        if (opts.length > 0)
        {
            config.append("</config>");
            return config.toString();
        }
        else
        {
            return null;
        }
    }
}