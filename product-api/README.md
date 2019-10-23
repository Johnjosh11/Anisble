# product-api
This API will provide product category related details.

Product api would provide the data in a format that's needed for the product management tool and product category use cases for webshop, ie. 
the product api would provide: 
The category hierarchy, products in a specific category 
Categories of a product/device_type 
Endpoints for updating the above mappings (and creating new categories)

For publishing to central artifactory(https://artifactory.saunalahti.fi) use below target

> mvn deploy -s settings.xml 
