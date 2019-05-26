import os
import sys
import django
import datetime
import json
from django.conf import settings

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'inventoryms.settings')
django.setup()

from inventory.models import *
from inventory.utils.tokenization import create_token
####### UserZone ####################33
norte = UserZone.objects.create(name="Norte")
sur = UserZone.objects.create(name="Sur")
este = UserZone.objects.create(name="Este")
oeste = UserZone.objects.create(name="Oeste")

######## admin user ###
admin_1 = User.objects.create_superuser(email="admin1@hotmail.com", password="adminadmin", phoneNumber='+41524204242')
admin_1.address = "Norte 1"
admin_1.userZone = norte
admin_1.role = 0
token = create_token(admin_1)
admin_1.token = str(token)
admin_1.name = "admin 1"
admin_1.save()

admin_2 = User.objects.create_superuser(email="admin2@hotmail.com", password="adminadmin", phoneNumber='+41524204243')
admin_2.address = "Sur 2"
admin_2.userZone = sur
admin_2.role = 0
token = create_token(admin_2)
admin_2.token = str(token)
admin_2.name = "admin 2"
admin_2.save()

######## client user ###
client_1 = User.objects.create_superuser(email="client1@hotmail.com", password="adminadmin", phoneNumber='+41524204244')
client_1.address = "Norte 2"
client_1.userZone = norte
client_1.role = 1
token = create_token(client_1)
client_1.token = str(token)
client_1.name = "client 1"
client_1.save()

client_2 = User.objects.create_superuser(email="client2@hotmail.com", password="adminadmin", phoneNumber='+41524204245')
client_2.address = "Sur 2"
client_2.userZone = sur
client_2.role = 1
token = create_token(client_2)
client_2.token = str(token)
client_2.name = "client 2"
client_2.save()

client_3 = User.objects.create_superuser(email="client3@hotmail.com", password="adminadmin", phoneNumber='+41524204246')
client_3.address = "Este 1"
client_3.userZone = este
client_3.role = 1
token = create_token(client_3)
client_3.token = str(token)
client_3.name = "client 3"
client_3.save()

######## DELIVERY_MAN ###
delivery_1 = User.objects.create_superuser(email="delivery1@hotmail.com", password="adminadmin", phoneNumber='+41524204247')
delivery_1.address = "Norte 3"
delivery_1.userZone = norte
delivery_1.role = 2
token = create_token(delivery_1)
delivery_1.token = str(token)
delivery_1.name = "delivery 1"
delivery_1.save()

delivery_2 = User.objects.create_superuser(email="delivery2@hotmail.com", password="adminadmin", phoneNumber='+41524204248')
delivery_2.address = "Sur 3"
delivery_2.userZone = sur
delivery_2.role = 2
token = create_token(delivery_2)
delivery_2.token = str(token)
delivery_2.name = "delivery 2"
delivery_2.save()

delivery_3 = User.objects.create_superuser(email="delivery3@hotmail.com", password="adminadmin", phoneNumber='+41524204249')
delivery_3.address = "Este 3"
delivery_3.userZone = este
delivery_3.role = 2
token = create_token(delivery_3)
delivery_3.token = str(token)
delivery_3.name = "delivery 3"
delivery_3.save()

delivery_4 = User.objects.create_superuser(email="delivery4@hotmail.com", password="adminadmin", phoneNumber='+41524204250')
delivery_4.address = "Oeste 1"
delivery_4.userZone = oeste
delivery_4.role = 2
token = create_token(delivery_4)
delivery_4.token = str(token)
delivery_4.name = "delivery 4"
delivery_4.save()

category_1 = Category.objects.create(name="frutas")
category_2 = Category.objects.create(name="verduras")
category_3 = Category.objects.create(name="organico")
category_4 = Category.objects.create(name="arroz")

product_1 = Product.objects.create(name="ciruela",typeSelling="unit",price=12.2,category=category_1)
product_1.photo = "1.jpg"
product_1.save()

product_2 = Product.objects.create(name="guaba",typeSelling="unit",price=14.2,category=category_1)
product_2.photo = "2.jpg"
product_2.save()

product_3 = Product.objects.create(name="pimiento",typeSelling="unit",price=14.2,category=category_2)
product_3.photo = "2.jpg"
product_3.save()

product_4 = Product.objects.create(name="tomate",typeSelling="unit",price=14.2,category=category_2)
product_4.photo = "2.jpg"
product_4.save()

product_5 = Product.objects.create(name="dulce de leche",typeSelling="unit",price=14.2,category=category_3)
product_5.photo = "2.jpg"
product_5.save()

product_6 = Product.objects.create(name="huevo",typeSelling="unit",price=14.2,category=category_3)
product_6.photo = "2.jpg"
product_6.save()

product_7 = Product.objects.create(name="arroz corriente",typeSelling="unit",price=14.2,category=category_4)
product_7.photo = "2.jpg"
product_7.save()

product_8 = Product.objects.create(name="arroz grano largo",typeSelling="unit",price=14.2,category=category_4)
product_8.photo = "2.jpg"
product_8.save()

centro_1 = DeliveryCenter.objects.create(name="Principal, Paipay", latitudeGeo=2.1, longitudeGeo=3.1)
centro_2 = DeliveryCenter.objects.create(name="Secundario, Norte", latitudeGeo=3.1, longitudeGeo=2.1)
centro_3 = DeliveryCenter.objects.create(name="Secundario, Sur", latitudeGeo=4.1, longitudeGeo=4.1)

####### pedidos ########################
products_list_1 = []
print(product_2.id)
products_list_1.append({"id":product_1.id.urn[9:], "cantidad": 2})
products_list_1.append({"id":product_2.id.urn[9:], "cantidad": 3})
products_list_1= json.dumps(products_list_1)
print(product_2.id)

products_list_2 = []
products_list_2.append({"id":product_3.id.urn[9:], "cantidad": 4})
products_list_2.append({"id":product_4.id.urn[9:], "cantidad": 5})
products_list_2.append({"id":product_5.id.urn[9:], "cantidad": 3})
products_list_2.append({"id":product_5.id.urn[9:], "cantidad": 6})
products_list_2= json.dumps(products_list_2)

products_list_3 = []
products_list_3.append({"id":product_6.id.urn[9:], "cantidad": 3})
products_list_3.append({"id":product_7.id.urn[9:], "cantidad": 4})
products_list_3= json.dumps(products_list_3)
########### estado solIcitado
SOLICITADO = 0
purchase_1 = Purchase.objects.create(deliveryCenter=centro_1,totalPrice=12.2, status=SOLICITADO)
purchase_1.products = products_list_1
purchase_1.save()
purchase_1.user.add(client_1)
purchase_1.user.add(delivery_1)
purchase_1.save()

purchase_2 = Purchase.objects.create(deliveryCenter=centro_1,totalPrice=12.2, status=SOLICITADO)
purchase_2.products = products_list_2
purchase_2.save()
purchase_2.user.add(client_2)
purchase_2.user.add(delivery_1)
purchase_2.save()

purchase_3 = Purchase.objects.create(deliveryCenter=centro_1,totalPrice=12.2, status=SOLICITADO)
purchase_3.products = products_list_3
purchase_3.save()
purchase_3.user.add(client_3)
purchase_3.user.add(delivery_1)
purchase_3.save()

purchase_4 = Purchase.objects.create(deliveryCenter=centro_1,totalPrice=12.2, status=SOLICITADO)
purchase_4.products = products_list_1
purchase_4.save()
purchase_4.user.add(client_1)
purchase_4.user.add(delivery_1)
purchase_4.save()

purchase_5 = Purchase.objects.create(deliveryCenter=centro_1,totalPrice=13.2, status=SOLICITADO)
purchase_5.products = products_list_2
purchase_5.save()
purchase_5.user.add(client_2)
purchase_5.user.add(delivery_2)
purchase_5.save()

purchase_6 = Purchase.objects.create(deliveryCenter=centro_1,totalPrice=13.2, status=SOLICITADO)
purchase_6.products = products_list_2
purchase_6.save()
purchase_6.user.add(client_1)
purchase_6.user.add(delivery_2)
purchase_6.save()

purchase_7 = Purchase.objects.create(deliveryCenter=centro_1,totalPrice=13.2, status=SOLICITADO)
purchase_7.products = products_list_2
purchase_7.save()
purchase_7.user.add(client_3)
purchase_7.user.add(delivery_2)
purchase_7.save()

purchase_8 = Purchase.objects.create(deliveryCenter=centro_1,totalPrice=13.2, status=SOLICITADO)
purchase_8.products = products_list_3
purchase_8.save()
purchase_8.user.add(client_3)
purchase_8.user.add(delivery_3)
purchase_8.save()

purchase_9 = Purchase.objects.create(deliveryCenter=centro_1,totalPrice=13.2, status=SOLICITADO)
purchase_9.products = products_list_1
purchase_9.save()
purchase_9.user.add(client_3)
purchase_9.user.add(delivery_3)
purchase_9.save()

########### estado armado
ARMADO = 1
purchase_10 = Purchase.objects.create(deliveryCenter=centro_1,totalPrice=13.2, status=ARMADO, barCode="000000000001")
purchase_10.products = products_list_1
purchase_10.save()
purchase_10.user.add(client_1)
purchase_10.user.add(delivery_1)
purchase_10.save()

purchase_11 = Purchase.objects.create(deliveryCenter=centro_1,totalPrice=13.2, status=ARMADO, barCode="000000000002")
purchase_11.products = products_list_1
purchase_11.save()
purchase_11.user.add(client_2)
purchase_11.user.add(delivery_1)
purchase_11.save()

purchase_12 = Purchase.objects.create(deliveryCenter=centro_1,totalPrice=13.2, status=ARMADO, barCode="000000000003")
purchase_12.products = products_list_1
purchase_12.save()
purchase_12.user.add(client_3)
purchase_12.user.add(delivery_1)
purchase_12.save()

purchase_13 = Purchase.objects.create(deliveryCenter=centro_1,totalPrice=13.2, status=ARMADO, barCode="000000000004")
purchase_13.products = products_list_1
purchase_13.save()
purchase_13.user.add(client_2)
purchase_13.user.add(delivery_2)
purchase_13.save()

purchase_14 = Purchase.objects.create(deliveryCenter=centro_1,totalPrice=13.2, status=ARMADO, barCode="000000000005")
purchase_14.products = products_list_1
purchase_14.save()
purchase_14.user.add(client_3)
purchase_14.user.add(delivery_2)
purchase_14.save()
