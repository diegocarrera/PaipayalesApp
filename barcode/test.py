import barcode
CANTIDAD = 10
print(barcode.PROVIDED_BARCODES)
ean13 = barcode.get_barcode_class('code128')
for i in range(CANTIDAD):
    code = str(i)
    x = ["0" for cero in range(12-len(code)) ]
    id = "".join(x) + code
    codigo = ean13(id)
    codigo.save(id)

