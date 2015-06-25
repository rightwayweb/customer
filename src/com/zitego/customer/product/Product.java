package com.zitego.customer.product;

import com.zitego.customer.delivery.DeliveryMethod;
import com.zitego.customer.delivery.DeliveryMethodType;
import com.zitego.customer.delivery.DeliveryMethodFactory;
import com.zitego.customer.pricing.VolumePricingConfig;
import com.zitego.customer.pricing.PricingFactory;
import com.zitego.customer.pricing.PricingType;
import com.zitego.customer.pricing.Pricing;
import com.zitego.sql.DatabaseEntity;
import com.zitego.sql.DBHandle;
import com.zitego.sql.DBConfig;
import com.zitego.sql.NoDataException;
import com.zitego.sql.PreparedStatementSupport;
import com.zitego.sql.UniqueNameDatabaseEntity;
import com.zitego.util.Constant;
import com.zitego.util.StatusType;
import com.zitego.util.TextUtils;
import java.text.ParseException;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Vector;
import java.text.DecimalFormat;
import java.math.BigDecimal;

/**
 * <p>
 * This class describes a product. Every product has a reference string that uniquely
 * identifies it. It also has a name that is displayed, an optional product number,
 * a @see Pricing, object that specifies how the product is to be priced, optional
 * search keywords, whether to apply state and country tax, inventory count, max quantity
 * allowed to be purchased, image urls for thumbnail image, standard image, and enlarged
 * image, a short description, a long description, a @see DeliveryMethod, related
 * products, product options, categories the product belongs to, and any custom
 * values for the product.
 * </p>
 * <p>
 * The Pricing object is used when getPrice(PriceArguments) is called. If the Pricing is of
 * type RegularPrice, then the regular price amount is returned. If it is SalePrice, then
 * the sale price is returned. if it is OptionBased a price based on the ProductOption[]
 * passed in is returned. If it is VolumeBased, then a price based on the volume passed in
 * is returned.
 * </p>
 * <p>
 * The @see DeliveryMethod is used to specify how shipping fees are calculated. Additionally,
 * the product can be set to allow store pickup or grant free shipping.
 * </p>
 * <p>
 * If the product is set for option based pricing, @see ProductOption objects need to
 * be configured for it.
 * </p>
 * <p>
 * Lastly, @see CustomValue objects can be set to allow additional values to be displayed
 * for this product.
 * </p>
 *
 * @author John Glorioso
 * @version $Id: Product.java,v 1.7 2012/11/06 03:37:54 jglorioso Exp $
 */
public class Product extends UniqueNameDatabaseEntity
{
    public static final DecimalFormat PRICE_FORMAT = new DecimalFormat("$#,##0.00");
    private String _productNumber;
    private Pricing _pricing;
    private float _regularPrice = -1f;
    private float _salePrice = -1f;
    private VolumePricingConfig _volumePricingConfig;
    private boolean _isRecurring = false;
    private String _searchKeywords;
    private boolean _applyCategoryDiscount = true;
    private boolean _applyStateTax = true;
    private boolean _applyCountryTax = true;
    // Causes isTrackingInventory() to return true if >= 0
    private int _inventoryCount = -1;
    // Causes hasMaxQuantitySet() to return true if > 0
    private int _maxQuantity = -1;
    private String _thumbnailImageUrl;
    private String _standardImageUrl;
    private String _enlargedImageUrl;
    private String _shortDescription;
    private String _longDescription;
    private DeliveryMethod _deliveryMethod;
    private boolean _allowStorePickup = false;
    private boolean _freeShipping = false;
    private Vector _relatedProducts = new Vector();
    private Vector _options = new Vector();
    private Vector _customValues = new Vector();

    /**
     * Creates a new Product with a DBHandle.
     *
     * @param db The db handle to use for querying.
     * @deprecated Use Product(DBConfig) instead.
     */
    public Product(DBHandle db)
    {
        this( db.getConfig() );
    }

    /**
     * Creates a new Product with a DBHandle.
     *
     * @param config The db config to use for querying.
     */
    public Product(DBConfig config)
    {
        super(config);
    }

    /**
     * Creates a new Product with an id and DBHandle.
     *
     * @param id The id.
     * @param db The db handle to use for querying.
     * @deprecated Use Product(long, DBConfig) instead.
     */
    public Product(long id, DBHandle db)
    {
        this( id, db.getConfig() );
    }

    /**
     * Creates a new Product with an id and DBHandle.
     *
     * @param id The id.
     * @param config The db config to use for querying.
     */
    public Product(long id, DBConfig config)
    {
        super(id, config);
    }

    /**
     * Creates a new Product with a universal identifier and DBHandle.
     *
     * @param id The universal identifier.
     * @param db The db handle to use for querying.
     * @deprecated Use Product(String, DBConfig) instead.
     */
    public Product(String id, DBHandle db)
    {
        this(id, db.getConfig() );
    }

    /**
     * Creates a new Product with a universal identifier and DBHandle.
     *
     * @param id The universal identifier.
     * @param config The db handle config to use for querying.
     */
    public Product(String id, DBConfig config)
    {
        super(config);
        setName(id);
    }

    public void init() throws SQLException, NoDataException
    {
        DBHandle db = getDBHandle();
        PreparedStatementSupport supp = new PreparedStatementSupport();
        StringBuffer sql = new StringBuffer()
            .append("SELECT product_id, reference_string name, reference_string, label, product_number, pricing_type_id, regular_price, sale_price, ")
            .append(       "volume_pricing_config, search_keywords, apply_category_discount, apply_state_tax, apply_country_tax, ")
            .append(       "inventory_count, max_quantity, thumbnail_image_url, standard_image_url, enlarged_image_url, short_description, ")
            .append(       "long_description, delivery_method_type_id, delivery_method_config, allow_store_pickup, free_shipping, ")
            .append(       "related_products, status, creation_date, last_updated ")
            .append("FROM product ")
            .append("WHERE ").append( getUniqueConstraint(supp) );
        supp.setSql(sql);

        db.connect();
        try
        {
            PreparedStatement pst = supp.bindValues(db);
            ResultSet rs = pst.executeQuery();

            if ( !rs.next() )
            {
                throw new NoDataException( "No such product: id-" + getId() + ", name-" + getName() );
            }

            setId( rs.getLong("product_id") );
            loadFromResultSet(rs);

            loadRelatedProducts( rs.getString("related_products") );
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * This method will load the properties of this product from the result set.
     * This will load the name, description, price,
     * status, creation_date, and last_updated columns. The caller is responsible for setting
     * the id. It is also assumed that .next() has been called and
     * the result set is ready to be read from.
     *
     * @throws SQLException if a database error occurs.
     */
    public void loadFromResultSet(ResultSet rs) throws SQLException
    {
        setReferenceString( rs.getString("reference_string") );
        setName( rs.getString("name") );
        setLabel( rs.getString("label") );
        setProductNumber( rs.getString("product_number") );
        setRegularPrice( rs.getFloat("regular_price") );
        if ( rs.wasNull() ) setRegularPrice(-1);
        setSalePrice( rs.getFloat("sale_price") );
        if ( rs.wasNull() ) setSalePrice(-1);
        String cfg = rs.getString("volume_pricing_config");
        if (cfg != null)
        {
            try
            {
                setVolumePricingConfig( new VolumePricingConfig(cfg) );
            }
            catch (ParseException pe)
            {
                throw new RuntimeException("Could not create the volume pricing config", pe);
            }
        }
        PricingType ptype = PricingType.evaluate( rs.getInt("pricing_type_id") );
        setPricing
        (
            PricingFactory.createPricing
            (
                ptype, getRegularPrice(), getSalePrice(), getVolumePricingConfig()
            )
        );
        setSearchKeywords( rs.getString("search_keywords") );
        setApplyCategoryDiscount(rs.getInt("apply_category_discount") == 1);
        setApplyStateTax( (rs.getInt("apply_state_tax") == 1) );
        setApplyCountryTax( (rs.getInt("apply_country_tax") == 1) );
        setInventoryCount( rs.getInt("inventory_count") );
        if ( rs.wasNull() ) setInventoryCount(-1);
        setMaxQuantity( rs.getInt("max_quantity") );
        if ( rs.wasNull() ) setMaxQuantity(-1);
        setThumbnailImageUrl( rs.getString("thumbnail_image_url") );
        setStandardImageUrl( rs.getString("standard_image_url") );
        setEnlargedImageUrl( rs.getString("enlarged_image_url") );
        setShortDescription( rs.getString("short_description") );
        setLongDescription( rs.getString("long_description") );
        setDeliveryMethod
        (
            DeliveryMethodFactory.getDeliveryMethod
            (
                DeliveryMethodType.evaluate( rs.getInt("delivery_method_type_id") ), rs.getString("delivery_method_config")
            )
        );
        setAllowStorePickup( (rs.getInt("allow_store_pickup") == 1) );
        setFreeShipping( (rs.getInt("free_shipping") == 1) );
        setStatus( rs.getInt("status") );
        setCreationDate( rs.getTimestamp("creation_date") );
        setLastUpdated( rs.getTimestamp("last_updated") );
    }

    /**
     * Loads a list of related products based on a comma delimited string of ids.
     *
     * @param idstr The ids.
     * @throws SQLException if a database error occurs.
     */
    protected void loadRelatedProducts(String idstr) throws SQLException
    {
        if ( idstr != null && !"".equals(idstr) )
        {
            DBHandle db = getDBHandle();
            StringBuffer sql = new StringBuffer()
                .append("SELECT product_id, reference_string, label FROM product WHERE product_id IN (").append(idstr).append(") AND status = ?");
            db.connect();
            try
            {
                PreparedStatement pst = db.prepareStatement(sql);
                pst.setInt( 1, StatusType.ACTIVE.getValue() );
                ResultSet rs = pst.executeQuery();
                _relatedProducts = new Vector();
                while ( rs.next() )
                {
                    Product prod = createProduct( rs.getLong("product_id"), getDBConfig() );
                    prod.setName( rs.getString("reference_string") );
                    prod.setLabel( rs.getString("label") );
                    _relatedProducts.add(prod);
                }
                String[] orderedIds = TextUtils.split(idstr, ',');
                int size = _relatedProducts.size();
                for (int i=0; i<orderedIds.length; i++)
                {
                    long id = Long.parseLong(orderedIds[i]);
                    for (int j=0; j<size; j++)
                    {
                        Product p = (Product)_relatedProducts.get(j);
                        if ( id == p.getId() )
                        {
                            Product p2 = (Product)_relatedProducts.get(i);
                            _relatedProducts.setElementAt(p, i);
                            _relatedProducts.setElementAt(p2, j);
                            break;
                        }
                    }
                }
            }
            finally
            {
                db.disconnect();
            }
        }
    }

    protected void update() throws SQLException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("UPDATE product SET reference_string = ?, label = ?, product_number = ?, pricing_type_id = ?, regular_price = ?, sale_price = ?, ")
            .append(       "volume_pricing_config = ?, search_keywords = ?, apply_category_discount = ?, apply_state_tax = ?, ")
            .append(       "apply_country_tax = ?, inventory_count = ?, max_quantity = ?, thumbnail_image_url = ?, standard_image_url = ?, ")
            .append(       "enlarged_image_url = ?, short_description = ?, long_description = ?, delivery_method_type_id = ?, ")
            .append(       "delivery_method_config = ?, allow_store_pickup = ?, free_shipping = ?, related_products = ?, status = ? ")
            .append("WHERE product_id = ?");
        db.connect();
        try
        {
            int n = 1;
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setString( n++, getName() );
            pst.setString( n++, getLabel() );
            pst.setString( n++, getProductNumber() );
            pst.setInt( n++, getPricing().getType().getValue() );
            if (getRegularPrice() > -1) pst.setFloat( n++, getRegularPrice() );
            else pst.setNull(n++, Types.NUMERIC);
            if (getSalePrice() > -1) pst.setFloat( n++, getSalePrice() );
            else pst.setNull(n++, Types.NUMERIC);
            if (getVolumePricingConfig() != null) pst.setString( n++, getVolumePricingConfig().toString() );
            else pst.setString(n++, null);
            pst.setString( n++, getSearchKeywords() );
            pst.setInt( n++, (getApplyCategoryDiscount() ? 1 : 0) );
            pst.setInt( n++, (getApplyStateTax() ? 1 : 0) );
            pst.setInt( n++, (getApplyCountryTax() ? 1 : 0) );
            if (getInventoryCount() > -1) pst.setInt( n++, getInventoryCount() );
            else pst.setNull(n++, Types.NUMERIC);
            if (getMaxQuantity() > -1) pst.setInt( n++, getMaxQuantity() );
            else pst.setNull(n++, Types.NUMERIC);
            pst.setString( n++, getThumbnailImageUrl() );
            pst.setString( n++, getStandardImageUrl() );
            pst.setString( n++, getEnlargedImageUrl() );
            pst.setString( n++, getShortDescription() );
            pst.setString( n++, getLongDescription() );
            pst.setInt( n++, getDeliveryMethod().getType().getValue() );
            pst.setString( n++, getDeliveryMethod().getConfig() );
            pst.setInt( n++, (getAllowStorePickup() ? 1 : 0) );
            pst.setInt( n++, (getFreeShipping() ? 1 : 0) );
            Vector tmp = getRelatedProducts();
            int size = tmp.size();
            StringBuffer ids = new StringBuffer();
            for (int i=0; i<size; i++)
            {
                Product p = (Product)tmp.get(i);
                ids.append( (i > 0 ? "," : "") ).append( p.getId() );
            }
            pst.setString( n++, (size > 0 ? ids.toString() : null) );
            pst.setInt(n++, getStatus() );
            pst.setLong( n++, getId() );
            pst.executeUpdate();
        }
        finally
        {
            db.disconnect();
        }
    }

    protected void insert() throws SQLException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("INSERT INTO product ")
            .append(    "(reference_string, label, product_number, pricing_type_id, regular_price, sale_price, volume_pricing_config, ")
            .append(     "search_keywords, apply_category_discount, apply_state_tax, apply_country_tax, inventory_count, max_quantity, ")
            .append(     "thumbnail_image_url, standard_image_url, enlarged_image_url, short_description, long_description, ")
            .append(     "delivery_method_type_id, delivery_method_config, allow_store_pickup, free_shipping, related_products, status, ")
            .append(     "creation_date) ")
            .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())");

        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            int n = 1;
            pst.setString( n++, getName() );
            pst.setString( n++, getLabel() );
            pst.setString( n++, getProductNumber() );
            pst.setInt( n++, getPricing().getType().getValue() );
            if (getRegularPrice() > -1) pst.setFloat( n++, getRegularPrice() );
            else pst.setNull(n++, Types.NUMERIC);
            if (getSalePrice() > -1) pst.setFloat( n++, getSalePrice() );
            else pst.setNull(n++, Types.NUMERIC);
            if (getVolumePricingConfig() != null) pst.setString( n++, getVolumePricingConfig().toString() );
            else pst.setString(n++, null);
            pst.setString( n++, getSearchKeywords() );
            pst.setInt( n++, (getApplyCategoryDiscount() ? 1 : 0) );
            pst.setInt( n++, (getApplyStateTax() ? 1 : 0) );
            pst.setInt( n++, (getApplyCountryTax() ? 1 : 0) );
            if (getInventoryCount() > -1) pst.setInt( n++, getInventoryCount() );
            else pst.setNull(n++, Types.NUMERIC);
            if (getMaxQuantity() > -1) pst.setInt( n++, getMaxQuantity() );
            else pst.setNull(n++, Types.NUMERIC);
            pst.setString( n++, getThumbnailImageUrl() );
            pst.setString( n++, getStandardImageUrl() );
            pst.setString( n++, getEnlargedImageUrl() );
            pst.setString( n++, getShortDescription() );
            pst.setString( n++, getLongDescription() );
            pst.setInt( n++, getDeliveryMethod().getType().getValue() );
            pst.setString( n++, getDeliveryMethod().getConfig() );
            pst.setInt( n++, (getAllowStorePickup() ? 1 : 0) );
            pst.setInt( n++, (getFreeShipping() ? 1 : 0) );
            Vector tmp = getRelatedProducts();
            int size = tmp.size();
            StringBuffer ids = new StringBuffer();
            for (int i=0; i<size; i++)
            {
                Product p = (Product)tmp.get(i);
                ids.append( (i > 0 ? "," : "") ).append( p.getId() );
            }
            pst.setString( n++, (size > 0 ? ids.toString() : null) );
            pst.setInt(n++, getStatus() );
            pst.executeUpdate();
            setId( db.getLastId(null) );
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Loads the product options that belong to this product in the given order. This will
     * load ProductOptions which do not include the options for each choice. Only the
     * id, name, and price.
     *
     * @throws SQLException
     */
    public void loadProductOptions() throws SQLException
    {
        DBHandle db = getDBHandle();
        db.connect();
        try
        {
            StringBuffer sql = new StringBuffer()
                .append("SELECT DISTINCT po.product_option_id, po.label, po.description, po.option_type_id, ")
                .append(       "po.required, po.err_msg, po.addl_html, po.status po_status, poc.product_option_choice_id, ")
                .append(       "poc.label choice_label, poc.value choice_value, poc.price_adjustment, poc.status poc_status ")
                .append("FROM product p, product_option po ")
                .append(    "LEFT JOIN product_option_choice poc ON (po.product_option_id = poc.product_option_id AND poc.status = ?) ")
                .append("WHERE p.product_id = ? AND p.product_id = po.product_id AND po.status = ? ")
                .append("ORDER BY po.order_id, poc.order_id");
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setInt( 1, StatusType.ACTIVE.getValue() );
            pst.setLong( 2, getId() );
            pst.setInt( 3, StatusType.ACTIVE.getValue() );
            ResultSet rs = pst.executeQuery();

            _options = new Vector();
            ProductOption po = null;
            long lastOption = -1;
            long opt = -1;
            while ( rs.next() )
            {
                opt = rs.getLong("product_option_id");
                if (opt != lastOption)
                {
                    addProductOption(po);
                    po = createProductOption( opt, getDBConfig() );
                    po.setProductId( getId() );
                    po.setLabel( rs.getString("label") );
                    if ( "".equals(po.getLabel()) ) po.setLabel("[BLANK LABEL]");
                    po.setDescription( rs.getString("description") );
                    po.setType( ProductOptionType.evaluate(rs.getInt("option_type_id")) );
                    po.setRequired( (rs.getInt("required") == 1) );
                    po.setErrMsg( rs.getString("err_msg") );
                    po.setAdditionalHtml( rs.getString("addl_html") );
                    po.setStatus( rs.getInt("po_status") );
                    lastOption = opt;
                }
                long choiceId = rs.getLong("product_option_choice_id");
                if ( !rs.wasNull() )
                {
                    ProductOptionChoice poc = po.createProductOptionChoice( choiceId, getDBConfig() );
                    poc.setProductOptionId( po.getId() );
                    poc.setLabel( rs.getString("choice_label") );
                    poc.setValue( rs.getString("choice_value") );
                    if ( "".equals(poc.getLabel()) ) poc.setLabel("[BLANK LABEL]");
                    poc.setPriceAdjustment( rs.getFloat("price_adjustment") );
                    if ( rs.wasNull() ) poc.setPriceAdjustment(0);
                    poc.setStatus( rs.getInt("poc_status") );
                    po.addChoice(poc);
                }
            }
            addProductOption(po);
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Saves the the product options in the product. It will delete the rows from the
     * database, then re-insert them.
     *
     * @throws SQLException
     */
    public void saveProductOptionOrder() throws SQLException
    {
        DBHandle db = getDBHandle();
        db.connect();
        try
        {
            StringBuffer sql = new StringBuffer()
                .append("DELETE FROM product_option WHERE product_id = ?");

            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            pst.executeUpdate();

            sql.setLength(0);
            sql.append("INSERT INTO product_option (product_id, product_option_id, order_id) ")
               .append("VALUES (?, ?, ?)");
            pst = db.prepareStatement(sql);
            int size = _options.size();
            for (int i=0; i<size; i++)
            {
                ProductOption opt = (ProductOption)_options.get(i);
                pst.setLong( 1, getId() );
                pst.setLong( 2, opt.getId() );
                pst.setInt( 3, (i+1) );
                pst.executeUpdate();
            }
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Sets the product reference string. Note that this will only be written to the database on
     * an insert command, so changing it here will not help you.
     *
     * @param id The identifier.
     */
    public void setReferenceString(String id)
    {
        setName(id);
    }

    /**
     * Returns the universal identifier.
     *
     * @return String
     */
    public String getReferenceString()
    {
        return getName();
    }

    /**
     * Sets the product number.
     *
     * @param name The product number.
     */
    public void setProductNumber(String num)
    {
        _productNumber = num;
    }

    /**
     * Returns the product number.
     *
     * @return String
     */
    public String getProductNumber()
    {
        return _productNumber;
    }

    /**
     * Sets the pricing.
     *
     * @param pricing The pricing.
     */
    public void setPricing(Pricing pricing)
    {
        _pricing = pricing;
    }

    /**
     * Returns the pricing.
     *
     * @return Pricing
     */
    public Pricing getPricing()
    {
        return _pricing;
    }

    /**
     * Sets the regular price.
     *
     * @param price The regular price.
     */
    public void setRegularPrice(float price)
    {
        _regularPrice = price;
    }

    /**
     * Returns the regular price.
     *
     * @return float
     */
    public float getRegularPrice()
    {
        return _regularPrice;
    }

    /**
     * Sets the sale price.
     *
     * @param price The sale price.
     */
    public void setSalePrice(float price)
    {
        _salePrice = price;
    }

    /**
     * Returns the sale price.
     *
     * @return float
     */
    public float getSalePrice()
    {
        return _salePrice;
    }

    /**
     * Sets the volume pricing config.
     *
     * @param config The volume pricing config.
     */
    public void setVolumePricingConfig(VolumePricingConfig config)
    {
        _volumePricingConfig = config;
    }

    /**
     * Sets the volume pricing config based on the config string passed in.
     *
     * @param config The volume pricing config.
     * @throws ParseException if an error occurs parsing the volume pricing.
     */
    public void setVolumePricingConfig(String config) throws ParseException
    {
        setVolumePricingConfig( new VolumePricingConfig(config) );
    }

    /**
     * Returns the volume pricing config.
     *
     * @return String
     */
    public VolumePricingConfig getVolumePricingConfig()
    {
        return _volumePricingConfig;
    }

    /**
     * Sets whether this product has recurring pricing or not.
     *
     * @param recurring The recurring pricing flag.
     */
    public void setRecurring(boolean recurring)
    {
        _isRecurring = recurring;
    }

    /**
     * Returns whether this product has recurring pricing.
     *
     * @return boolean
     */
    public boolean isRecurring()
    {
        return _isRecurring;
    }

    /**
     * Sets the search keywords.
     *
     * @param keywords The search keywords.
     */
    public void setSearchKeywords(String keywords)
    {
        _searchKeywords = keywords;
    }

    /**
     * Returns the search keywords.
     *
     * @return String
     */
    public String getSearchKeywords()
    {
        return _searchKeywords;
    }

    /**
     * Sets whether to apply any category discounts or not.
     *
     * @param flag The apply discounts flag.
     */
    public void setApplyCategoryDiscount(boolean flag)
    {
        _applyCategoryDiscount = flag;
    }

    /**
     * Returns whether this product should have category discounts applied to it.
     *
     * @return boolean
     */
    public boolean getApplyCategoryDiscount()
    {
        return _applyCategoryDiscount;
    }

    /**
     * Sets whether to apply any state taxes or not.
     *
     * @param flag The state tax flag.
     */
    public void setApplyStateTax(boolean flag)
    {
        _applyStateTax = flag;
    }

    /**
     * Returns whether this product apply state taxes.
     *
     * @return boolean
     */
    public boolean getApplyStateTax()
    {
        return _applyStateTax;
    }

    /**
     * Sets whether to apply any country taxes or not.
     *
     * @param flag The country tax flag.
     */
    public void setApplyCountryTax(boolean flag)
    {
        _applyCountryTax = flag;
    }

    /**
     * Returns whether this product apply country taxes.
     *
     * @return boolean
     */
    public boolean getApplyCountryTax()
    {
        return _applyCountryTax;
    }

    /**
     * Sets the inventory count.
     *
     * @param count The inventory count.
     */
    public void setInventoryCount(int count)
    {
        _inventoryCount = count;
    }

    /**
     * Returns the inventory count.
     *
     * @return int
     */
    public int getInventoryCount()
    {
        return _inventoryCount;
    }

    /**
     * Sets the max quantity allowed to be sold. -1 means no limit.
     *
     * @param quantity The max quantity.
     */
    public void setMaxQuantity(int quantity)
    {
        _maxQuantity = quantity;
    }

    /**
     * Returns the max quantity.
     *
     * @return int
     */
    public int getMaxQuantity()
    {
        return _maxQuantity;
    }

    /**
     * Sets the thumbnail url.
     *
     * @param url The thumbnail url.
     */
    public void setThumbnailImageUrl(String url)
    {
        _thumbnailImageUrl = url;
    }

    /**
     * Returns the thumbnail url.
     *
     * @return String
     */
    public String getThumbnailImageUrl()
    {
        return _thumbnailImageUrl;
    }

    /**
     * Sets the standard url.
     *
     * @param url The standard url.
     */
    public void setStandardImageUrl(String url)
    {
        _standardImageUrl = url;
    }

    /**
     * Returns the standard url.
     *
     * @return String
     */
    public String getStandardImageUrl()
    {
        return _standardImageUrl;
    }

    /**
     * Sets the enlarged url.
     *
     * @param url The enlarged url.
     */
    public void setEnlargedImageUrl(String url)
    {
        _enlargedImageUrl = url;
    }

    /**
     * Returns the enlarged url.
     *
     * @return String
     */
    public String getEnlargedImageUrl()
    {
        return _enlargedImageUrl;
    }

    /**
     * Sets the short description.
     *
     * @param description The short description.
     */
    public void setShortDescription(String description)
    {
        _shortDescription = description;
    }

    /**
     * Returns the short description.
     *
     * @return String
     */
    public String getShortDescription()
    {
        return _shortDescription;
    }

    /**
     * Sets the long description.
     *
     * @param description The long description.
     */
    public void setLongDescription(String description)
    {
        _longDescription = description;
    }

    /**
     * Returns the long description.
     *
     * @return String
     */
    public String getLongDescription()
    {
        return _longDescription;
    }

    /**
     * Sets the delivery method.
     *
     * @param method The delivery method.
     */
    public void setDeliveryMethod(DeliveryMethod method)
    {
        _deliveryMethod = method;
    }

    /**
     * Returns the delivery method.
     *
     * @return DeliveryMethod
     */
    public DeliveryMethod getDeliveryMethod()
    {
        return _deliveryMethod;
    }

    /**
     * Sets whether to allow store pickup for this product or not.
     *
     * @param flag The store pickup flag.
     */
    public void setAllowStorePickup(boolean flag)
    {
        _allowStorePickup = flag;
    }

    /**
     * Returns whether to allow store pickup or not.
     *
     * @return boolean
     */
    public boolean getAllowStorePickup()
    {
        return _allowStorePickup;
    }

    /**
     * Sets whether or not there is free shipping on this product.
     *
     * @param flag The free shipping flag.
     */
    public void setFreeShipping(boolean flag)
    {
        _freeShipping = flag;
    }

    /**
     * Returns whether this product has free shipping.
     *
     * @return boolean
     */
    public boolean getFreeShipping()
    {
        return _freeShipping;
    }

    /**
     * Adds a related product to this product.
     *
     * @param prod The related product.
     */
    public void addRelatedProduct(Product prod)
    {
        if (prod != null) _relatedProducts.add(prod);
    }

    /**
     * Returns the related products associated with this product.
     *
     * @return Vector
     */
    public Vector getRelatedProducts()
    {
        return _relatedProducts;
    }

    /**
     * Returns the related product with the specified id.
     *
     * @param id The related product id.
     * @return Product
     */
    public Product getRelatedProduct(long id)
    {
        int size = _relatedProducts.size();
        for (int i=0; i<size; i++)
        {
            Product p = (Product)_relatedProducts.get(i);
            if (p.getId() == id) return p;
        }
        return null;
    }

    /**
     * Removes the related product.
     *
     * @param id The related product.
     */
    public void removeRelatedProduct(long id)
    {
        Product p = getRelatedProduct(id);
        if (p != null) _relatedProducts.remove(p);
    }

    /**
     * Moves a related product up or down in order based on the id and the direction parameter.
     * A value greater then 0 will be moved up and a number less or equal to 0 will be moved down.
     * If it cannot be moved in the specified direction, nothing will happen.
     *
     * @param id The product id.
     * @param int The direction.
     */
    public void moveRelatedProduct(long id, int dir)
    {
        if (dir > 0) dir = 1;
        else dir = -1;
        Product p = getRelatedProduct(id);
        if (p != null)
        {
            int index = _relatedProducts.indexOf(p);
            index += dir;
            if ( index >= 0 && index < _relatedProducts.size() )
            {
                _relatedProducts.remove(p);
                _relatedProducts.insertElementAt(p, index);
            }
        }
    }

    /**
     * Returns all active products as a Vector of Constants. The id is the value and the
     * name is the name. If there are no products, then an empty Vector is returned.
     *
     * @param db The database handle to use.
     * @param rest The restaurant id.
     * @throws SQLException
     */
    public static Vector getAllProducts(DBHandle db, long rest) throws SQLException
    {
        Vector ret = new Vector();
        db.connect();
        try
        {
            StringBuffer sql = new StringBuffer()
                .append("SELECT product_id, name ")
                .append("FROM product ")
                .append("WHERE status = ? ")
                .append("ORDER BY label");
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setInt( 1, StatusType.ACTIVE.getValue() );
            ResultSet rs = pst.executeQuery();
            while ( rs.next() )
            {
                ret.add( new Constant(rs.getInt("product_id"), rs.getString("name")) );
            }
        }
        finally
        {
            db.disconnect();
        }
        return ret;
    }

    /**
     * Adds a product option to this product.
     *
     * @param opt The product option.
     */
    public void addProductOption(ProductOption opt)
    {
        if (opt != null) _options.add(opt);
    }

    /**
     * Returns the product options associated with this product.
     *
     * @return Vector
     */
    public Vector getProductOptions()
    {
        return _options;
    }

    /**
     * Returns the product option with the specified id.
     *
     * @param id The product option id.
     * @return ProductOption
     */
    public ProductOption getProductOption(long id)
    {
        int size = _options.size();
        for (int i=0; i<size; i++)
        {
            ProductOption p = (ProductOption)_options.get(i);
            if (p.getId() == id) return p;
        }
        return null;
    }

    /**
     * Removes the product option.
     *
     * @param id The product option.
     */
    public void removeProductOption(long id)
    {
        ProductOption p = getProductOption(id);
        if (p != null) _options.remove(p);
    }

    /**
     * Sets the product options.
     *
     * @param options The product options.
     */
    public void setOptions(Vector options)
    {
        if (options == null) options = new Vector();
        _options = options;
    }

    /**
     * Moves a product option up or down in order based on the id and the direction parameter.
     * A value greater then 0 will be moved up and a number less or equal to 0 will be moved down.
     * If it cannot be moved in the specified direction, nothing will happen.
     *
     * @param id The product option id.
     * @param int The direction.
     */
    public void moveProductOption(long id, int dir)
    {
        if (dir > 0) dir = 1;
        else dir = -1;
        ProductOption p = getProductOption(id);
        if (p != null)
        {
            int index = _options.indexOf(p);
            index += dir;
            if ( index >= 0 && index < _options.size() )
            {
                _options.remove(p);
                _options.insertElementAt(p, index);
            }
        }
    }

    public Vector getParents() throws SQLException
    {
        Vector ret = new Vector();
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("SELECT cp.category_id, c.name, c.label ")
            .append("FROM category_product cp, category c ")
            .append("WHERE cp.product_id = ? AND cp.category_id = c.category_id ")
            .append("ORDER BY label");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            ResultSet rs = pst.executeQuery();
            while ( rs.next() )
            {
                Category c = createCategory( rs.getLong("category_id"), getDBConfig() );
                c.setName( rs.getString("name") );
                c.setLabel( rs.getString("label") );
                ret.add(c);
            }
        }
        finally
        {
            db.disconnect();
        }
        return ret;
    }

    /**
     * Returns the customer values associated with this product.
     *
     * @return Vector
     */
    public Vector getCustomValues()
    {
        return _customValues;
    }

    /**
     * Returns the customer value with the specified name.
     *
     * @param name The custom value name that uniquely identifies it.
     * @return CustomValue
     */
    public CustomValue getCustomValue(String name)
    {
        int size = _customValues.size();
        for (int i=0; i<size; i++)
        {
            CustomValue c = (CustomValue)_customValues.get(i);
            if (c.getName() == name) return c;
        }
        return null;
    }

    /**
     * Removes the custom value.
     *
     * @param name The custom value name that uniquely identifies it.
     */
    public void removeCustomValue(String name)
    {
        CustomValue c = getCustomValue(name);
        if (c != null) _customValues.remove(c);
    }

    /**
     * Formats the given float as a price. It uses half round up for formatting
     * unlike the DecimalFormat class.
     *
     * @param price The price.
     * @return String
     */
    public static String formatPrice(float price)
    {
        BigDecimal result = new BigDecimal(price);
        result = result.setScale(2, BigDecimal.ROUND_HALF_UP);
        return PRICE_FORMAT.format( result.floatValue() );
    }

    /**
     * Sets the reference string.
     *
     * @param id The identifier.
     * @deprecated Use setReferenceString
     */
    public void setUniversalIdentifier(String id)
    {
        setReferenceString(id);
    }

    /**
     * Returns the reference string.
     *
     * @return String
     * @deprecated Use getReferenceString
     */
    public String getUniversalIdentifier()
    {
        return getReferenceString();
    }

    /**
     * Sets the regular price.
     *
     * @param price The price.
     * @deprecated Use setRegularPrice
     */
    public void setPrice(float price)
    {
        setRegularPrice(price);
    }

    /**
     * Returns the regular price.
     *
     * @return String
     * @deprecated Use getRegularPrice
     */
    public float getPrice()
    {
        return getRegularPrice();
    }

    /**
     * Sets the short description.
     *
     * @param desc The description.
     * @deprecated Use setShortDescription or setLongDescription.
     */
    public void setDescription(String desc)
    {
        setShortDescription(desc);
    }

    /**
     * Returns the short description.
     *
     * @return String
     */
    public String getDescription()
    {
        return getShortDescription();
    }

    /**
     * Used to create a product option.
     *
     * @param id The product option id.
     * @param db The database handle.
     */
    public ProductOption createProductOption(long id, DBHandle db)
    {
        return createProductOption( id, getDBConfig() );
    }

    public ProductOption createProductOption(long id, DBConfig config)
    {
        return new ProductOption(id, config);
    }

    /**
     * Used to create a product.
     *
     * @param id The product id.
     * @param db The database handle.
     * @deprecated Use createProduct(long, DBConfig) instead.
     */
    public Product createProduct(long id, DBHandle db)
    {
        return createProduct( id, db.getConfig() );
    }

    public Product createProduct(long id, DBConfig config)
    {
        return new Product(id, config);
    }

    /**
     * Used to create a category.
     *
     * @param id The category id.
     * @param db The database handle.
     * @deprecated Use createCategory(long, DBConfig) instead.
     */
    public Category createCategory(long id, DBHandle db)
    {
        return createCategory( id, db.getConfig() );
    }

    public Category createCategory(long id, DBConfig config)
    {
        return new Category(id, config);
    }

    public String getIdColName()
    {
        return "product_id";
    }

    public String getNameColName()
    {
        return "reference_string";
    }

    public int getQuantity(ProductOptionChoice... choices) throws SQLException
    {
        if (choices.length == 0) return 0;

        int qty = 0;
        DBHandle db = getDBHandle();
        db.connect();
        try
        {
            PreparedStatementSupport support = new PreparedStatementSupport();
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT quantity FROM product_option_choice_quantity WHERE ");
            int count = 0;
            for (ProductOptionChoice choice : choices)
            {
                sql.append( (count++ > 0 ? "AND " : "") ).append("product_choice").append(count).append("_id = ? ");
                support.add( choice.getId() );
            }
            support.setSql(sql);
            PreparedStatement pst = support.bindValues(db);
            ResultSet rs = pst.executeQuery();
            if ( rs.next() ) qty = rs.getInt(1);
        }
        finally
        {
            db.disconnect();
        }
        return qty;
    }
}
