RELEASE NOTES:

Dependencies - servlet.jar, common, markup

1.3.7  - Gutted product options and choices of unique name.
       - Added quantity to product_option_choice combos.
       - Removed n:m product->options and options->choices and changed
         to parent/child rows
       - Added international support for Paypal

1.3.6  - Added paypal payment gateway.
       - Added order id to pre-authorize transaction.
       - Added resp_code and avs_code to CustomerOrder and CreditCardResponse.

1.3.5  - Added Orbital payment gateway credit card api implementation.
       - Added additional html property to product option.
       - Conformed to new DatabaseEntity model with DBConfig.
       - Added getHtmlDetailSummary to CustomerOrderItem.
       - Bug fix in product ordering in the category class where product id ordering
         is needed before product option order id.
       - Removed subscriptions.
       - Fixed volume pricing by making total reflect it.
       - Changed signature of chargeCard to include order id and removed email lines.

1.3.4  - Added Ready for Pickup customer order status.

1.3.3  - Removed database load of transaction type and hard coded.
       - Removed database load of result type and hard coded.
       - Removed database load of pricing type and hard coded.
       - Removed database load of delivery method type and hard coded.

1.3.2  - Added ability to set order description and invoice number in AimCreditCardAPI on charges.
       - Added custom fields to Customer

1.3.1  - Changed so that regular price return sales price if set.

1.3    - More robust delivery implementation.

1.2.3  - SQL bug fix on load items in CustomerOrder.
       - SQL bug fix on load details in CustomerOrderItem.
       - Added encryptDataToHex and DecryptDataFromHex to com.zitego.customer.creditCard.CcEncryption.

1.2.2  - Added ResultType.PENDING
       - Added TransactionType.OFFLINE
       - Added setUrl methods to credit card api subclasses for reflection.
       - Changed CreditCardType to a regular Constant by removing the loadData method
         body.
       - Changed CustomerOrderItemDetail to not have null price. Make no price set = 0.
       - Forced the password to be sent on auth.net transactions if it is not null.

1.2.1  - Modifications of CustomerOrderItem and addition of CustomerOrderItemDetail.
       - Set new ContactInformation and BillingInformation on construction of Customer.
       - Added ResultType and TransactionType to CustomerOrder.

1.2    - Re-work of product model to be compatible with new shopping cart application.

1.1.2  - Added cvv2 number to customer, billing info, and billing_info.jsp

1.1.1  - Added CcEncryption and CreditCardStore interface.
       - Made AimCreditCardAPI able to add, edit, and delete credit cards via a credit store.
       - Added createSubscriptionFeatureOption to CustomerSubscriptionDetail.
       - Added preauthorizing, processing preauthorized transactions, and crediting credit cards.
       - Added remove feature and get feature at in Subscription.
       - Added void transaction to CreditCardAPI.

1.1    - Added basic Authorize.net credit card processing.

1.0.2e - Added clearFeatures to subscription.
       - Added protected constructor to SubscriptionType for extensions.

1.0.2d - Added getter methods for credit card api.
       - Added CreditCard class.
       - Added constructor in BillingInformation to build with a ContactInformation object.
       - Moved sendRequest methods from cdg api to generic credit card api.
       - Added CDGHtml classes.
       - Changed all double to float. This WILL AFFECT DEPENDANT CLASSES.
       - Changed to check null cc info on save.

1.0.2a - Made the customer class independent of the zitego database.

1.0.2  - Refactored subscription model to include feature options for better extensibility.
       - Added subscription type.

1.0.1b - Added getCustomerSubscriptions to ProductManager.
       - Fixes to customer.jsp to allow for no credit card information.
       - Changed to not initialize product id as -1 in CustomerSubscription.

1.0.1a - Compiled using latest common, markup, and web and changed StaticProperties references
         to only be used if the BaseConfigServlet.getWebappProperties() is not present.

1.0.1  - Changed to use new standalone common and markup jars.
       - Redesign in order to accomodate a more flexible pricing structure for subscriptions.

1.0    - Initial Release
