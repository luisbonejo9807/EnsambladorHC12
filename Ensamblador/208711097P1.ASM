	  ORG   $E000
INI       ldaa  #$D3    ;asigna al registro denomunado acumulador A D3H
	  ldab  #00     ;asigana al regidtro denominado acumulador B lo que
	                ;exista en ka direccion de memoria (0000H)
	  adda  #$AB    ;A=A+ABH
	  addb  $02     ;B=B+(0002H)
	  jmp   INI
	  END
