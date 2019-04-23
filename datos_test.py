import os
import sys
import django
import datetime
from django.conf import settings

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'inventoryms.settings')
django.setup()

from inventory.models import *
"""
user_c = User.objects.create(email="mau_leiton96@hotmail.com",
    password="a1234567890", name="coordinador", role=0)

centro_1 = DeliveryCenter.objects.create(name="Principal, Paipay", latitudeGeo=2.1, longitudeGeo=3.1)
centro_2 = DeliveryCenter.objects.create(name="Secundario, Norte", latitudeGeo=3.1, longitudeGeo=2.1)
"""
Purchase.objects.create(deliveryCenter=centro_1)