package com.zitego.customer.product;

import com.zitego.customer.delivery.DeliveryMethodType;
import com.zitego.customer.delivery.DeliveryMethodFactory;
import com.zitego.customer.pricing.VolumePricingConfig;
import com.zitego.customer.pricing.PricingFactory;
import com.zitego.customer.pricing.PricingType;
import com.zitego.sql.DatabaseEntity;
import com.zitego.sql.DBHandle;
import com.zitego.sql.DBConfig;
import com.zitego.sql.NoDataException;
import com.zitego.sql.PreparedStatementSupport;
import com.zitego.sql.UniqueNameDatabaseEntity;
import com.zitego.util.StatusType;
import com.zitego.util.Constant;
import java.text.ParseException;
import java.util.Vector;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

/**
 * This class represents a category of products/subcategories. A category has
 * one or more products within it. Categories have a name, and an optional
 * comment to be displayed. A category can belong to multiple
 * categories or can be the root. It also has a number of products.
 *
 * @author John Glorioso
 * @version $Id: Category.java,v 1.5 2011/10/16 17:20:34 jglorioso Exp $
 */
public class Category extends UniqueNameDatabaseEntity
{
    private String _imageUrl;
    private String _description;
    private DiscountType _discountType;
    private float _discountAmount = 0f;
    private String _discountNote;
    private Vector _products = new Vector();

    /**
     * Creates a new Category with a DBHandle.
     *
     * @param db The db handle to use for querying.
     * @deprecated Use Category(DBConfig) instead.
     */
    public Category(DBHandle db)
    {
        super( db.getConfig() );
    }

    /**
     * Creates a new Category with a DBConfig.
     *
     * @param config The db config to use for querying.
     */
    public Category(DBConfig config)
    {
        super(config);
    }

    /**
     * Creates a new Category with a DBHandle.
     *
     * @param id The id.
     * @param db The db handle to use for querying.
     * @deprecated Use Category(long, DBConfig) instead.
     */
    public Category(long id, DBHandle db)
    {
        this( id, db.getConfig() );
    }

    /**
     * Creates a new Category with a DBConfig.
     *
     * @param id The id.
     * @param congig The db config to use for querying.
     */
    public Category(long id, DBConfig config)
    {
        super(id, config);
    }

    /**
     * Creates a new Category with a unique name and DBHandle.
     *
     * @param id The name.
     * @param db The db handle to use for querying.
     * @deprecated Use Category(String, DBConfig) instead.
     */
    public Category(String id, DBHandle db)
    {
        this( id, db.getConfig() );
    }

    /**
     * Creates a new Category with a unique name and DBHandle.
     *
     * @param id The name.
     * @param config The db handle config to use for querying.
     */
    public Category(String id, DBConfig config)
    {
        super(config);
        setName(id);
    }

    public void init() throws SQLException, NoDataException
    {
        DBHandle db = getDBHandle();
        PreparedStatementSupport supp = new PreparedStatementSupport();
        StringBuffer sql = new StringBuffer()
            .append("SELECT category_id, name, label, image_url, description, discount_type_id, discount_amount, discount_note, ")
            .append(       "status, creation_date, last_updated ")
            .append("FROM category ")
            .append("WHERE ").append( getUniqueConstraint(supp) );
        supp.setSql(sql);

        db.connect();
        try
        {
            PreparedStatement pst = supp.bindValues(db);
            ResultSet rs = pst.executeQuery();

            if ( !rs.next() ) throw new NoDataException( "No such category : id-" + getId() + ", name -" + getName() );
            setId( rs.getLong("category_id") );
            setName( rs.getString("name") );
            setLabel( rs.getString("label") );
            _imageUrl = rs.getString("image_url");
            _description = rs.getString("description");
            _discountType = DiscountType.evaluate( rs.getInt("discount_type_id") );
            _discountAmount = rs.getFloat("discount_amount");
            if ( rs.wasNull() ) _discountAmount = -1;
            _discountNote = rs.getString("discount_note");
            setStatus( rs.getInt("status") );
            setCreationDate( rs.getTimestamp("creation_date") );
            setLastUpdated( rs.getTimestamp("last_updated") );
        }
        finally
        {
            db.disconnect();
        }
    }

    protected void update() throws SQLException
    {
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("UPDATE category SET name = ?, label = ?, image_url = ?, description = ?, discount_type_id = ?, ")
            .append(       "discount_amount = ?, discount_note = ?, status = ?  ")
            .append("WHERE category_id = ?");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setString( 1, getName() );
            pst.setString( 2, getLabel() );
            pst.setString(3, _imageUrl);
            pst.setString(4, _description);
            if (_discountType != null)
            {
                pst.setInt( 5, _discountType.getValue() );
                pst.setFloat(6, _discountAmount);
                pst.setString(7, _discountNote);
            }
            else
            {
                pst.setNull(5, Types.NUMERIC);
                pst.setNull(6, Types.NUMERIC);
                pst.setString(7, _discountNote);
            }
            pst.setInt( 8, getStatus() );
            pst.setLong( 9, getId() );
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
            .append("INSERT INTO category ")
            .append(    "(name, label, image_url, description, discount_type_id, discount_amount, discount_note, status, creation_date) ")
            .append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, now())");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setString( 1, getName() );
            pst.setString( 2, getLabel() );
            pst.setString(3, _imageUrl);
            pst.setString(4, _description);
            if (_discountType != null)
            {
                pst.setInt( 5, _discountType.getValue() );
                pst.setFloat(6, _discountAmount);
                pst.setString(7, _discountNote);
            }
            else
            {
                pst.setNull(5, Types.NUMERIC);
                pst.setNull(6, Types.NUMERIC);
                pst.setString(7, _discountNote);
            }
            pst.setInt( 8, getStatus() );
            pst.executeUpdate();
            setId( db.getLastId(null) );
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Sets the image url.
     *
     * @param url The image url.
     */
    public void setImageUrl(String url)
    {
        _imageUrl = url;
    }

    /**
     * Returns the image url.
     *
     * @return String
     */
    public String getImageUrl()
    {
        return _imageUrl;
    }

    /**
     * Sets the description.
     *
     * @param desc The description.
     */
    public void setDescription(String desc)
    {
        _description = desc;
    }

    /**
     * Returns the description.
     *
     * @return String
     */
    public String getDescription()
    {
        return _description;
    }

    /**
     * Sets the discount type.
     *
     * @param type The discount type.
     */
    public void setDiscountType(DiscountType type)
    {
        _discountType = type;
    }

    /**
     * Returns the discount type.
     *
     * @return String
     */
    public DiscountType getDiscountType()
    {
        return _discountType;
    }

    /**
     * Sets the discount amount.
     *
     * @param amt The discount amount.
     */
    public void setDiscountAmount(float amt)
    {
        _discountAmount = amt;
    }

    /**
     * Returns the discount amount.
     *
     * @return String
     */
    public float getDiscountAmount()
    {
        return _discountAmount;
    }

    /**
     * Sets the discount note.
     *
     * @param note The discount note.
     */
    public void setDiscountNote(String note)
    {
        _discountNote = note;
    }

    /**
     * Returns the discount note.
     *
     * @return String
     */
    public String getDiscountNote()
    {
        return _discountNote;
    }

    /**
     * Loads the products that belong to this category in the given order. This will
     * load Products including the options for each product, but not the choices
     * for each option. All other information about the product will be loaded.
     *
     * @throws SQLException
     */
    public void loadProducts() throws SQLException
    {
        DBHandle db = getDBHandle();
        db.connect();
        try
        {
            StringBuffer sql = new StringBuffer()
                .append("SELECT DISTINCT p.product_id, p.reference_string name, p.label, p.product_number, p.pricing_type_id, p.regular_price, ")
                .append(       "p.sale_price, p.volume_pricing_config, p.search_keywords, p.apply_category_discount, ")
                .append(       "p.apply_state_tax, p.apply_country_tax, p.inventory_count, p.max_quantity, p.thumbnail_image_url, ")
                .append(       "p.standard_image_url, p.enlarged_image_url, p.short_description, p.long_description, ")
                .append(       "p.delivery_method_type_id, p.delivery_method_config, p.allow_store_pickup, p.free_shipping, ")
                .append(       "po.product_option_id, po.label polabel, po.description, po.option_type_id, ")
                .append(       "po.required, po.err_msg, po.addl_html ")
                .append("FROM category_product cp, product p ")
                .append(    "LEFT JOIN product_option po ON (p.product_id = po.product_id AND po.status = ?) ")
                .append("WHERE cp.category_id = ? AND cp.product_id = p.product_id AND p.status = ? ")
                .append("ORDER BY cp.order_id, p.product_id, po.order_id");
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setInt(1, 1);
            pst.setLong( 2, getId() );
            pst.setInt(3, 1);
            ResultSet rs = pst.executeQuery();
            _products = new Vector();
            Product p = null;
            long lastProd = -1;
            long prod = -1;
            while ( rs.next() )
            {
                prod = rs.getLong("product_id");
                if (prod != lastProd)
                {
                    addProduct(p);
                    p = createProduct( prod, getDBConfig() );
                    p.setName( rs.getString("name") );
                    p.setLabel( rs.getString("label") );
                    p.setProductNumber( rs.getString("product_number") );
                    p.setRegularPrice( rs.getFloat("regular_price") );
                    if ( rs.wasNull() ) p.setRegularPrice(-1);
                    p.setSalePrice( rs.getFloat("sale_price") );
                    if ( rs.wasNull() ) p.setSalePrice(-1);
                    String cfg = rs.getString("volume_pricing_config");
                    if (cfg != null)
                    {
                        try
                        {
                            p.setVolumePricingConfig( new VolumePricingConfig(cfg) );
                        }
                        catch (ParseException pe)
                        {
                            throw new RuntimeException("Could not create the volume pricing config", pe);
                        }
                    }
                    p.setPricing
                    (
                        PricingFactory.createPricing
                        (
                            PricingType.evaluate( rs.getInt("pricing_type_id") ),
                            p.getRegularPrice(), p.getSalePrice(), p.getVolumePricingConfig()
                        )
                    );
                    p.setSearchKeywords( rs.getString("search_keywords") );
                    p.setApplyCategoryDiscount(rs.getInt("apply_category_discount") == 1);
                    p.setApplyStateTax( (rs.getInt("apply_state_tax") == 1) );
                    p.setApplyCountryTax( (rs.getInt("apply_country_tax") == 1) );
                    p.setInventoryCount( rs.getInt("inventory_count") );
                    if ( rs.wasNull() ) p.setInventoryCount(-1);
                    p.setMaxQuantity( rs.getInt("max_quantity") );
                    if ( rs.wasNull() ) p.setMaxQuantity(-1);
                    p.setThumbnailImageUrl( rs.getString("thumbnail_image_url") );
                    p.setStandardImageUrl( rs.getString("standard_image_url") );
                    p.setEnlargedImageUrl( rs.getString("enlarged_image_url") );
                    p.setShortDescription( rs.getString("short_description") );
                    p.setLongDescription( rs.getString("long_description") );
                    p.setDeliveryMethod
                    (
                        DeliveryMethodFactory.getDeliveryMethod
                        (
                            DeliveryMethodType.evaluate( rs.getInt("delivery_method_type_id") ), rs.getString("delivery_method_config")
                        )
                    );
                    p.setAllowStorePickup( (rs.getInt("allow_store_pickup") == 1) );
                    p.setFreeShipping( (rs.getInt("free_shipping") == 1) );
                    lastProd = prod;
                }
                long optionId = rs.getLong("product_option_id");
                if ( !rs.wasNull() )
                {
                    ProductOption ppo = p.createProductOption( optionId, getDBConfig() );
                    ppo.setLabel( rs.getString("polabel") );
                    if ( "".equals(ppo.getLabel()) ) ppo.setLabel("[BLANK LABEL]");
                    ppo.setDescription( rs.getString("description") );
                    ppo.setType( ProductOptionType.evaluate(rs.getInt("option_type_id")) );
                    ppo.setRequired( (rs.getInt("required") == 1) );
                    ppo.setErrMsg( rs.getString("err_msg") );
                    ppo.setAdditionalHtml( rs.getString("addl_html") );
                    p.addProductOption(ppo);
                }
            }
            addProduct(p);
        }
        finally
        {
            db.disconnect();
        }
    }

    /**
     * Saves the the products in the category. It will delete the rows from the
     * database, then re-insert them.
     *
     * @throws SQLException
     */
    public void saveProductOrder() throws SQLException
    {
        DBHandle db = getDBHandle();
        db.connect();
        try
        {
            StringBuffer sql = new StringBuffer()
                .append("DELETE FROM category_product WHERE category_id = ?");

            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            pst.executeUpdate();

            sql.setLength(0);
            sql.append("INSERT INTO category_product (category_id, product_id, order_id) ")
               .append("VALUES (?, ?, ?)");
            pst = db.prepareStatement(sql);
            int size = _products.size();
            for (int i=0; i<size; i++)
            {
                Product prod = (Product)_products.get(i);
                pst.setLong( 1, getId() );
                pst.setLong( 2, prod.getId() );
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
     * Adds a product to this category.
     *
     * @param item The product to add.
     */
    public void addProduct(Product prod)
    {
        if (prod != null) _products.add(prod);
    }

    /**
     * Returns the product with the specified id.
     *
     * @param id The product id.
     * @return Product
     */
    public Product getProduct(long id)
    {
        int size = _products.size();
        for (int i=0; i<size; i++)
        {
            Product prod = (Product)_products.get(i);
            if (prod.getId() == id) return prod;
        }
        return null;
    }

    /**
     * Removes a product from this category based on the id.
     *
     * @param id The id of the product to remove.
     */
    public void removeProduct(long id)
    {
        Product prod = getProduct(id);
        if (prod != null) _products.remove(prod);
    }

    /**
     * Returns the category products.
     *
     * @return Vector
     */
    public Vector getProducts()
    {
        return _products;
    }

    /**
     * Moves a product up or down in order based on the id and the direction parameter.
     * A value greater then 0 will be moved up and a number less or equal to 0 will be moved down.
     * If it cannot be moved in the specified direction, nothing will happen.
     *
     * @param id The product id.
     * @param int The direction.
     */
    public void moveProduct(long id, int dir)
    {
        if (dir > 0) dir = 1;
        else dir = -1;
        Product prod = getProduct(id);
        if (prod != null)
        {
            int index = _products.indexOf(prod);
            index += dir;
            if ( index >= 0 && index < _products.size() )
            {
                _products.remove(prod);
                _products.insertElementAt(prod, index);
            }
        }
    }

    /**
     * Returns all active categories as a Vector of Constants. The id is the value and
     * the name is the description. If there are no categories, then an empty Vector is returned.
     *
     * @param db The database handle to use.
     * @throws SQLException
     */
    public static Vector getAllCategories(DBHandle db) throws SQLException
    {
        Vector ret = new Vector();
        db.connect();
        try
        {
            StringBuffer sql = new StringBuffer()
                .append("SELECT category_id, name ")
                .append("FROM category ")
                .append("WHERE status = ? ")
                .append("ORDER BY name");
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setInt( 1, StatusType.ACTIVE.getValue() );
            ResultSet rs = pst.executeQuery();
            while ( rs.next() )
            {
                ret.add( new Constant(rs.getInt("category_id"), rs.getString("name")) );
            }
        }
        finally
        {
            db.disconnect();
        }
        return ret;
    }

    public Vector getParents() throws SQLException
    {
        Vector ret = new Vector();
        DBHandle db = getDBHandle();
        StringBuffer sql = new StringBuffer()
            .append("SELECT cc.parent_category_id, c.name, c.label ")
            .append("FROM category_category cc, category c ")
            .append("WHERE cc.category_id = ? AND cc.parent_category_id = c.category_id ")
            .append("ORDER BY label");
        db.connect();
        try
        {
            PreparedStatement pst = db.prepareStatement(sql);
            pst.setLong( 1, getId() );
            ResultSet rs = pst.executeQuery();
            while ( rs.next() )
            {
                Category c = createCategory( rs.getLong("parent_category_id"), getDBConfig() );
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
        return new Category( id, db.getConfig() );
    }

    public Category createCategory(long id, DBConfig config)
    {
        return new Category(id, config);
    }

    public String getIdColName()
    {
        return "category_id";
    }
}
