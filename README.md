# Subnet-Calculator
Command Line Interface project for IP subnet calculation

For  a given IP V4 Address, calculates:

Address without the slash notation
Address:   XXX.XXX.XXX.XXX       BINARY 

Subnet mask, based on the slash notation. Part of the address used for network address
Netmask:   XXX.XXX.XXX.XXX = XX  BINARY

Wildcard, part of the address used for hosts
Wildcard:  0.0.127.255           00000000.00000000.0 1111111.11111111

Subnet address
Subnet           XXX.XXX.XXX.XXX/XX  00100000.00010100.1 0000000.00000000 (Class A)
Broadcast:       32.20.255.255   00100000.00010100.1 1111111.11111111
HostMin (FHIP):  32.20.128.1     00100000.00010100.1 0000000.00000001
HostMax (LHIP):  32.20.255.254   00100000.00010100.1 1111111.11111110
s=9
S=512
Subnet Index (000101001) = 41
h=15
HIPs Hosts/Net: 32766
