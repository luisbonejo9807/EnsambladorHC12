/*Alumno (Student):      Gómez Tovar Edgar Iván
  Código (Code):         303526879
  Materia (Signature):   Taller de programación de sistemas (Programing systems workshop)
  NRC:                   02316
  Ensamblador (assembler)*/

#include <cstdlib>
#include <iostream>
#include <stdio.h>
#include <stdlib.h>
#include <io.h>
#include <string.h>

using namespace std;

/*******************************************************************************************************************************************/
/*************************************************************Clase archivo*****************************************************************/
/*************************************************************Class archivo*****************************************************************/
/*******************************************************************************************************************************************/

class clasearchivo
{
      public:
          int fd,abierto,bandera,bandera2,bandera3,aux,i,j,sin_errores,error_codop,no_end,modos,org,tipo_codop,tipo,DIR_INIC,CONTLOC,limpia_archivos,limpia_tabsim,paso_2,linea_error,linea_S19,largo;
          char archivo[40],archivo2[80],archivo3[80],total_b,b_por_calc,temporal,letra,letra2,letra3,etiqueta[20],codop[15],operando[50],num_hex[20],cod_maq[8],binario[20],xb[6],binario_t[20],ch[3],etiqueta2[20],cod_maq2[80],tamano[3],direccion[5],direccion2[5],datos[33],cad_temp[2],datos_temp[80],datos_temp2[80],nombre_S19[80],nombre_t[80];

/***************************************************************Abrir***********************************************************************/

      void abrir()
      {
           printf("\nQue archivo quieres abrir: ");
           fflush(stdin);
           gets(archivo);
           fd=open(archivo, 0);
           if(fd>0)
              abierto=1;
           else
           {
              perror("\n\n  --- Error");
              abierto=0;
           }
           if(archivo[1]==':')
           {
              strcpy(archivo3,"C:\\");
              i=0;
              while(archivo[i]!='.')
                 i++;
              while(archivo[i]!='\\')
                 i--;
              i++;
              j=3;
              while(archivo[i]!='\0')
              {
                 archivo3[j]=archivo[i];
                 i++;
                 j++;
              }
              archivo3[j]='\0';
           }
           else
           {
              strcpy(archivo3,"C:\\");
              strcat(archivo3,archivo);
              archivo3[strlen(archivo)+3]='\0';
           }
           j=archivo3[0];
           dec_hex(j,1);  //Vamos a obtener el valor hexadecimal en 2 bytes del ASCII de cada caracter del nombre del archivo (We are going to take the hexadecimal value in 2 bytes from each character from the name of the file)
           strcpy(archivo2,num_hex);  //Archivo2 tiene el valor hexadecimal del primer caracter del nombre del archivo principal, en su (Archivo2 has the hexadecimal value from the first character from the name of the main file, in his)
           i=1;  //primera posición (nota que es strcpy y no strcat, por eso se separo del while) (first position)
           while(archivo3[i]!='\0')
           {
              j=archivo3[i];
              dec_hex(j,1);
              strcat(archivo2,num_hex);
              i++;
           }
           strcat(archivo2,"0A");
           archivo2[strlen(archivo2)]='\0'; //Archivo2 tiene archivo3 en ASCII, más "0A"(Archivo2 has archivo3 in ASCII, plus "0A")
      }

/****************************************************************Mostrar********************************************************************/

      void mostrar()
      {
           if(eof(fd))
              errores(1);
           else
           {
              limpia_archivos=1;  //Verifica si ya se debe sobreescribir el archivo TEMPORAL del archivo principal ensamblado (Verify if it´s the time to overwrite the TEMPORAL file from the main file)
              limpia_tabsim=0;  //Para sobreescribir el TABSIM (for overwrite the TABSIM)
              org=0;  //Indica si ya aparecio el ORG, 0=no 1=si (Indicate if a ORG directive have already show)
              DIR_INIC=-1;
              CONTLOC=-1000;
              largo=-1;
              while(read(fd,&temporal,1) && !eof(fd))
              {
                 linea_error=0;
                 bandera=0; //Indica si se identifico un comentario para no entrar en la segunda parte, 0=no 1=si  (Indicate if we have just identify a coment)
                 error_codop=0;  //Indica si ahy un salto de linea o fin de archivo despues de una etiqueta, 0=no 1=si
                 no_end=0;  //Indica si se encontro el codigo de operacion END, 0=si 1=no
                 tipo_codop=0;  //Indica el tipo de operando a usar en los archivos TEMPORAL y TABSIM, 1=directiva, 2=codop
                 linea_S19=0;  //Indica la acción a tomar en relación con el S19, 0=nada, 1=interrupción, 2=escribir registro S1

                 //----------------------------COMENTARIO--------------------------------------
                 //-----------------------------COMMENT----------------------------------------

                 if(temporal==';')
                 {
                    bandera2=0; //Indica si ahy más de un signo de punto y coma en el comentario, 0=no 1=si (Indicates if there is more than one signe of dot and comma in the comment, 0=not 1=yes)
                    i=1;
                    while(read(fd,&temporal,1) && temporal!='\n')
                    {
                       if(temporal==';')
                          bandera2=1;
                       i++;
                    }
                    if(bandera2==1)
                       errores(2);
                    if(i>80)
                       errores(3);
                    else if(i<80 && bandera2==0)
                       errores(4);
                    bandera=1;
                 }

                 //------------------------------NO COMENTARIO-----------------------------------
                 //-------------------------------NO COMMENT-------------------------------------

                 if(bandera==0)
                 {
                    if(temporal!='\n')
                    {

                       //-------------------------------ETIQUETA----------------------------------
                       //---------------------------------Label-----------------------------------

                       if(temporal==' ' || temporal=='\t')
                       {
                          strcpy(etiqueta,"null");
                       }
                       else
                       {
                          bandera=0; //Comprueba el primer caracter, 0=invalido 1=valido (Check the first character, 0=wrong 1=ok)
                          bandera2=0; //Comprueba si el primer caracter se repite, 0=no 1=si (Check if the first character is repeated, 0=no 1=yes)
                          bandera3=0; //Comprueba si los caracteres son validos, 0=validos 1=invalido (Check if all the charaters are ok, 0=ok 1=wrong)
                          if(temporal=='a' || temporal=='b' || temporal=='c' || temporal=='d' || temporal=='e' || temporal=='f' || temporal=='g' || temporal=='h' || temporal=='i' || temporal=='j' || temporal=='k' || temporal=='l' || temporal=='m' || temporal=='n' || temporal=='ñ' || temporal=='o' || temporal=='p' || temporal=='q' || temporal=='r' || temporal=='s' || temporal=='t' || temporal=='u' || temporal=='v' || temporal=='w' || temporal=='x' || temporal=='y' || temporal=='z' || temporal=='A' || temporal=='B' || temporal=='C' || temporal=='D' || temporal=='E' || temporal=='F' || temporal=='G' || temporal=='H' || temporal=='I' || temporal=='J' || temporal=='K' || temporal=='L' || temporal=='M' || temporal=='N' || temporal=='Ñ' || temporal=='O' || temporal=='P' || temporal=='Q' || temporal=='R' || temporal=='S' || temporal=='T' || temporal=='U' || temporal=='V' || temporal=='W' || temporal=='X' || temporal=='Y' || temporal=='Z')
                          {
                             bandera=1;
                             letra=temporal;
                          }
                          else
                             letra2=temporal;
                          etiqueta[0]=temporal;
                          i=1;
                          while(read(fd,&temporal,1) && temporal!=' ' && temporal!= '\t' && temporal!= '\n')
                          {
                             if(temporal==letra)
                                bandera2=1;
                             if(temporal!='a' && temporal!='b' && temporal!='c' && temporal!='d' && temporal!='e' && temporal!='f' && temporal!='g' && temporal!='h' && temporal!='i' && temporal!='j' && temporal!='k' && temporal!='l' && temporal!='m' && temporal!='n' && temporal!='ñ' && temporal!='o' && temporal!='p' && temporal!='q' && temporal!='r' && temporal!='s' && temporal!='t' && temporal!='u' && temporal!='v' && temporal!='w' && temporal!='x' && temporal!='y' && temporal!='z' && temporal!='A' && temporal!='B' && temporal!='C' && temporal!='D' && temporal!='E' && temporal!='F' && temporal!='G' && temporal!='H' && temporal!='I' && temporal!='J' && temporal!='K' && temporal!='L' && temporal!='M' && temporal!='N' && temporal!='Ñ' && temporal!='O' && temporal!='P' && temporal!='Q' && temporal!='R' && temporal!='S' && temporal!='T' && temporal!='U' && temporal!='V' && temporal!='W' && temporal!='X' && temporal!='Y' && temporal!='Z' && temporal!='0' && temporal!='1' && temporal!='2' && temporal!='3' && temporal!='4' && temporal!='5' && temporal!='6' && temporal!='7' && temporal!='8' && temporal!='9' && temporal!='_' )
                             {
                                letra3=temporal;
                                bandera3=1;
                             }
                             etiqueta[i]=temporal;
                             i++;
                          }
                          etiqueta[i]='\0';
                          if(bandera==0)
                             errores(5);
                          /*if(bandera2==1)
                             errores(6);*/
                          if(bandera3==1)
                             errores(7);
                          if(i>8)
                             errores(8);
                          if(temporal=='\n' || eof(fd))  //Si despues de la etiqueta no se puso el codop o se llego al fin de archivo (If after label there isn´t)
                             error_codop=1;
                          if(eof(fd) && no_end==1)  //Si despues de la etiqueta esta el fin de archivo y no se a encontrado el END (If after label we found the end of the file and we haven´t found the END directive)
                             no_end=0;
                       }

                       //------------------------------CODOP----------------------------------------

                       i=0;
                       bandera=0; //Indica si la primer letra es un caracter valido, 1=si 0=no (Check the first character, 0=ok 1=wrong)
                       bandera2=0; //Indica la validez de los demás caracteres, 0=valido 1=invalido (Check if all the charaters are ok, 0=ok 1=wrong)
                       bandera3=0; //Indica si se encuentra más de un punto en el codop, 0=no 1=si (Check if we found more than 1 dot character, 0=no 1=yes)
                       sin_errores=0;  //Para checar el TABOP, 1=sin errores 0=con errores (To check the TABOP, 1=without mistakes 0=with errors)
                       if(error_codop==0)
                       {
                          while(read(fd,&temporal,1)>0 && temporal!=' ' && temporal!='\t' && temporal!='\n')
                          {
                             codop[i]=temporal;
                             if(i==0)
                             {
                                if(codop[0]=='a' || codop[0]=='b' || codop[0]=='c' || codop[0]=='d' || codop[0]=='e' || codop[0]=='f' || codop[0]=='g' || codop[0]=='h' || codop[0]=='i' || codop[0]=='j' || codop[0]=='k' || codop[0]=='l' || codop[0]=='m' || codop[0]=='n' || codop[0]=='ñ' || codop[0]=='o' || codop[0]=='p' || codop[0]=='q' || codop[0]=='r' || codop[0]=='s' || codop[0]=='t' || codop[0]=='u' || codop[0]=='v' || codop[0]=='w' || codop[0]=='x' || codop[0]=='y' || codop[0]=='z' || codop[0]=='A' || codop[0]=='B' || codop[0]=='C' || codop[0]=='D' || codop[0]=='E' || codop[0]=='F' || codop[0]=='G' || codop[0]=='H' || codop[0]=='I' || codop[0]=='J' || codop[0]=='K' || codop[0]=='L' || codop[0]=='M' || codop[0]=='N' || codop[0]=='Ñ' || codop[0]=='O' || codop[0]=='P' || codop[0]=='Q' || codop[0]=='R' || codop[0]=='S' || codop[0]=='T' || codop[0]=='U' || codop[0]=='V' || codop[0]=='W' || codop[0]=='X' || codop[0]=='Y' || codop[0]=='Z')
                                   bandera=1;
                                letra=codop[0];
                             }
                             else
                             {
                                if(codop[i]!='a' && codop[i]!='b' && codop[i]!='c' && codop[i]!='d' && codop[i]!='e' && codop[i]!='f' && codop[i]!='g' && codop[i]!='h' && codop[i]!='i' && codop[i]!='j' && codop[i]!='k' && codop[i]!='l' && codop[i]!='m' && codop[i]!='n' && codop[i]!='ñ' && codop[i]!='o' && codop[i]!='p' && codop[i]!='q' && codop[i]!='r' && codop[i]!='s' && codop[i]!='t' && codop[i]!='u' && codop[i]!='v' && codop[i]!='w' && codop[i]!='x' && codop[i]!='y' && codop[i]!='z' && codop[i]!='A' && codop[i]!='B' && codop[i]!='C' && codop[i]!='D' && codop[i]!='E' && codop[i]!='F' && codop[i]!='G' && codop[i]!='H' && codop[i]!='I' && codop[i]!='J' && codop[i]!='K' && codop[i]!='L' && codop[i]!='M' && codop[i]!='N' && codop[i]!='Ñ' && codop[i]!='O' && codop[i]!='P' && codop[i]!='Q' && codop[i]!='R' && codop[i]!='S' && codop[i]!='T' && codop[i]!='U' && codop[i]!='V' && codop[i]!='W' && codop[i]!='X' && codop[i]!='Y' && codop[i]!='Z' && codop[i]!='.')
                                {
                                   bandera2=1;
                                   letra2=codop[i];
                                }
                                if(codop[i]=='.')
                                   bandera3++;
                             }
                             i++;
                          }
                          codop[i]='\0';
                          if(bandera==0)
                             errores(9);
                          if(bandera2==1)
                             errores(10);
                          if(bandera3>1)
                             errores(11);
                          if(i>5)
                             errores(12);
                          if(i<=5 && bandera==1 && bandera2==0 && bandera3<2)
                          {
                             sin_errores=1;
                          }
                       }
                       else if(error_codop==1 || eof(fd))
                       {
                          strcpy(operando,"null");
                          errores(13);
                       }

                       //-----------------------------OPERANDO---------------------------------------
                       //------------------------------OPERAND---------------------------------------

                       bandera=0;  //Indica si se encontro un salto de línea, 0=no 1=si (Indicates if we found a jump line, 0=no 1=yes)
                       if(temporal=='\n' || eof(fd))
                       {
                          strcpy(operando,"null");
                          bandera=1;
                       }
                       if(bandera==0)
                       {
                          read(fd,&temporal,1);
                          operando[0]=temporal;
                          i=1;
                          while(read(fd,&temporal,1) && temporal!='\n')
                          {
                             operando[i]=temporal;
                             i++;
                          }
                          operando[i]='\0';
                       }
                       if(sin_errores==1)  //Si el codop esta correcto puede comenzar a buscarlo como directiva y en el TABOP (If the codop is correct we can start to look for him as a directive or in the TABOP)
                       {
                          if(directivas()==1)    //Si directivas() regresa 1 es por que no es una directiva y puede ser codop (If directivas () return 1 it´s because this is not a directive and it can be a codop)
                             id_operando();  //Aquí lo busca como codop (Here we look as a codop)
                          if(largo==0)
                             for(j=0;j<32;j++)
                                datos[j]='\0';
                          if(linea_S19==2 && paso_2==1)  //Si es el segundo paso de ensamblado y la linea actual generó codigo maquina (If we currently are in the second step of the assembling and the current line actualy generates machine code)
                          {
                             strcpy(datos_temp,cod_maq2);
                             i=strlen(datos_temp)/2;
                             if(i+largo==16)
                             {
                                strcat(datos,datos_temp);
                                largo=19;
                                dec_hex(largo,1);
                                strcpy(tamano,num_hex);
                                checksum(tamano,direccion,datos);
                                S19("S1",tamano,direccion,datos,ch);
                                largo=0;
                                for(j=0;j<80;j++)
                                   datos_temp[j]='\0';
                                for(j=0;j<32;j++)
                                   datos[j]='\0';
                                tamano[0]='\0';
                             }
                             else if(i+largo<16)
                             {
                                if(largo==0)
                                   strcpy(datos,datos_temp);
                                else
                                   strcat(datos,datos_temp);
                                largo=largo+i;
                                datos[largo*2]='\0';
                                for(j=0;j<80;j++)
                                   datos_temp[j]='\0';
                             }
                             else if(i+largo>16)
                             {
                                j=(i+largo)-16;
                                aux=i-j;
                                i=0;
                                while(largo<16)
                                {
                                   datos[largo*2]=datos_temp[i];
                                   i++;
                                   datos[(largo*2)+1]=datos_temp[i];
                                   largo++;
                                   i++;
                                }
                                datos[32]='\0';
                                largo=19;
                                dec_hex(largo,1);
                                strcpy(tamano,num_hex);
                                checksum(tamano,direccion,datos);
                                S19("S1",tamano,direccion,datos,ch);
                                for(j=0;j<32;j++)
                                   datos[j]='\0';
                                largo=0;
                                while(datos_temp[i]!='\0' && largo<16)
                                {
                                   datos[largo*2]=datos_temp[i];
                                   i++;
                                   datos[(largo*2)+1]=datos_temp[i];
                                   i++;
                                   largo++;
                                }
                                datos[largo*2]='\0';
                                strcpy(direccion,direccion2);
                                j=extraer(direccion,16)+aux;
                                dec_hex(j,2);
                                strcpy(direccion,num_hex);
                                while(largo==16)
                                {
                                   largo=19;
                                   dec_hex(largo,1);
                                   strcpy(tamano,num_hex);
                                   checksum(tamano,direccion,datos);
                                   S19("S1",tamano,direccion,datos,ch);
                                   for(j=0;j<32;j++)
                                      datos[j]='\0';
                                   largo=0;
                                   while(datos_temp[i]!='\0' && largo<16)
                                   {
                                      datos[largo*2]=datos_temp[i];
                                      i++;
                                      datos[(largo*2)+1]=datos_temp[i];
                                      i++;
                                      largo++;
                                   }
                                   aux=aux+16;
                                   datos[largo*2]='\0';
                                   strcpy(direccion,direccion2);
                                   j=extraer(direccion,16)+aux;
                                   dec_hex(j,2);
                                   strcpy(direccion,num_hex);
                                }
                                //for(j=0;j<80;j++)
                                   //datos_temp[j]='\0';
                             }
                          }  //Fin del si se genero codigo maquina para meterlo en el S1 (End of the "if the actual line generates machine code")
                          if(linea_S19==1 && largo>0)  //Interrupción al registro S1 (Interrump to the register S1)
                          {
                             largo=largo+3;
                             dec_hex(largo,1);
                             strcpy(tamano,num_hex);
                             checksum(tamano,direccion,datos);
                             S19("S1",tamano,direccion,datos,ch);
                             for(j=0;j<32;j++)
                                datos[j]='\0';
                             for(j=0;j<80;j++)
                                datos_temp[j]='\0';
                             largo=0;
                          }
                          limpia_archivos=0;
                       }  //Fin del si el codop esta correcto (End of the "if the codop is correct")
                       etiqueta[0]='\0';
                       codop[0]='\0';
                       operando[0]='\0';
                    }  //Fin de la parte de no salto de línea (End of the part "no jump line")
                 }   //Fin de la parte de no comentario (End of the part "no comment")
              }  //Fin del while principal (End of the main while)
              if(no_end==0)
                 errores(14);
           }  //Fin del si el archivo no esta vacio (End of the "if the file isn´t empty")
           if(paso_2==0)
           {
              lseek(fd,0L,0);
              paso_2=1;
              mostrar();
              S19("S9","N","U","L","L");
           }
      }  //Fin de la función mostrar (End of the mostrar function)

/***********************************************************Busqueda_tabop******************************************************************/

      void busqueda_tabop()
      {
         int j,posicion,encontro,val_operando,operando_igual;
         char temporal2,codop2[10],operando2,mod_dir[15],b_calc,t_bytes,operando_2[50];
         tipo_codop=2;
         if(modos==1)
            strcpy(operando_2,"Inherente");
         else if(modos==2)
            strcpy(operando_2,"Inmediato");  //De 8 bits (Immediate of 8 bits)
         else if(modos==3)
            strcpy(operando_2,"Inmediato");  //De 16 bits (Immediate of 16 bits)
         else if(modos==4)
            strcpy(operando_2,"Directo");
         else if(modos==5)
            strcpy(operando_2,"Extendido");
         else if(modos==6)
            strcpy(operando_2,"IDX");  //Indizado de 5 bits (Indexed of 8 bits)
         else if(modos==7)
            strcpy(operando_2,"IDX1");  //Indizado de 9 bits (Indexed of 9 bits)
         else if(modos==8)
            strcpy(operando_2,"IDX2");  //Indizado de 16 bits (Indexed of 16 bits)
         else if(modos==9)
            strcpy(operando_2,"[IDX2]");  //Indizado indirecto de 16 bits (Indexed indirect of 16 bits)
         else if(modos==10)
            strcpy(operando_2,"IDX");  //Indizado de auto pre decremento (Indexed of auto pre decrement)
         else if(modos==11)
            strcpy(operando_2,"IDX");  //Indizado de auto post decremento (Indexed of auto post decrement)
         else if(modos==12)
            strcpy(operando_2,"IDX");  //Indizado de auto pre incremento (Indexed of auto pre increment)
         else if(modos==13)
            strcpy(operando_2,"IDX");  //Indizado de auto post incremento (Indexed of auto post increment)
         else if(modos==14)
            strcpy(operando_2,"IDX");  //Indizado de acumulador (Indexed of acumulator)
         else if(modos==15)
            strcpy(operando_2,"[D,IDX]");  //Indizado de acumulador indirecto (Indexed of acumulator indirect)
         else if(modos==16)
            strcpy(operando_2,"REL");  //Relativo de 16 bits (Relative of 16 bits)
         else if(modos==17)
            strcpy(operando_2,"REL");  //Relativo de 8 bits (Relative of 8 bits)
         else if(modos==18)
            strcpy(operando_2,"REL");  //Podría ser relativo o extendido (It could be relative or extend)
         posicion=lseek(fd,0L,1);
         close(fd);
         fd=open("TABOP.TXT", 0);
         if(fd>0)
         {
            lseek(fd,0L,0);
            encontro=0;  //Valida si se encontro concordancia entre el codop y el TABOP, 0=no 1=si (Validate if we found that the codop and the TABOP are the same, 0=no 1=yes)
            val_operando=0;  //Valida la existencia de un operando, 0=normal 1=se necesita operando y no se puso 2=si no se necesita operando y de todos modos se puso (Validate the existence of a operand, 0=normal 1=need a operand and it doesn´t exist 2=if we doesn´t need a operand and it´s already there)
            operando_igual=0;  //Si el operando coincide con lo que dice el TABOP.txt para el codop dado, 0=no 1=si (If the operand match with that inside the TABOP.txt for the actual codop, 0=no 1=yes)
            while(read(fd,&temporal2,1)>0 && operando_igual==0)
            {
               if(temporal2!='\n')
               {
                  codop2[0]=temporal2;
                  j=1;
                  while(read(fd,&temporal2,1) && temporal2!=' ' && temporal2!='\t')
                  {
                     codop2[j]=temporal2;
                     j++;
                  }
                  codop2[j]='\0';
                  if(strcmp(codop2,codop)==0)  //-----------------------------------------Si el codop esta en el TABOP.txt (If the codop is inside the TABOP.txt)
                  {
                     //----------------------------------------------Revisando el operando (Checking the operand)
                     read(fd,&temporal2,1);
                     operando2=temporal2;
                     if(operando2=='1')
                     {
                        if(strcmp("null",operando)==0)
                           val_operando=1;
                     }
                     else if(operando2=='0')
                     {
                        if(strcmp("null",operando)!=0)
                           val_operando=2;
                     }
                     read(fd,&temporal2,1);  //Aquí leemos un tabulador o un blanco (Here we read a tab or a space)
                     //----------------------------------------------Revisando el modo de direccionamiento (Checking the addressing mode)
                     j=0;
                     while(read(fd,&temporal2,1) && temporal2!=' ' && temporal2!='\t')
                     {
                        mod_dir[j]=temporal2;
                        j++;
                     }
                     mod_dir[j]='\0';
                     //----------------------------------------------Revisando el codigo maquina (Checking the machine code)
                     j=0;
                     while(read(fd,&temporal2,1) && temporal2!=' ' && temporal2!='\t')
                     {
                        cod_maq[j]=temporal2;
                        j++;
                     }
                     cod_maq[j]='\0';
                     //----------------------------------------------Revisando los bytes calculados (Checking the bytes calculates)
                     read(fd,&temporal2,1);  //Aquí leemos un tabulador o un blanco (Here we read a tab or a space)
                     b_calc=temporal2;
                     read(fd,&temporal2,1);  //Aquí leemos un tabulador o un blanco (Here we read a tab or a space)
                     //----------------------------------------------Revisando los bytes por calcular (Checking the bytes to calculate)
                     read(fd,&temporal2,1);  //Aquí leemos ahora si los bytes por calcular (Now we read the bytes to calculate)
                     b_por_calc=temporal2;
                     read(fd,&temporal2,1);  //Aquí leemos un tabulador o un blanco (Here we read a tab or a space)
                     //----------------------------------------------Revisando el total de bytes (Checking the total bytes)
                     read(fd,&temporal2,1);  //Aquí leemos ahora si el total de bytes (Now we read the total bytes)
                     t_bytes=temporal2;
                     encontro=1;  //Indica que si se encontro el codigo de operación en el Tabop.txt (Indicates if we found the operation code in the TABOP.txt)
                     if(strcmp(mod_dir,operando_2)==0)  //Indica que el modo de direccionamiento es soportado por el codop (Indicates tha the addressing mode it´s actualy suported by the codop)
                     {
                        operando_igual=1;
                        total_b=t_bytes;
                        if(modos==18)
                        {
                           if(codop[0]=='L' || codop[0]=='l')
                              modos=16;  //Relativo de 16 bits (Relative of 16 bits)
                           else
                              modos=17;  //Relativo de 8 bits (Relative of 8 bits)
                        }
                     }
                     codop2[0]='\0';
                     mod_dir[0]='\0';
                     //cod_maq[0]='\0';
                  }  //Fin del, si se encontro el codop (End of the "if we found the codop")
                  else if(strcmp(codop2,codop)!=0)
                  {
                     codop2[0]='\0';
                     while(read(fd,&temporal2,1)>0 && temporal2!='\n')
                     {
                     }
                  }
               }  //Fin del sino es salto de línea (End of the "if it isn´t the jump line")
            }  //Fin del while principal (End of the main while)
            if(operando_igual==0 && modos==18)  //Si el operando es una etiqueta y buscandola como relativo no se encuentra (If the operand is a label  and searching for it as a relative we doesn´t found it)
               modos=19;  //todavía se debe de buscar como extendido (We already have to check as a extended)
            if(encontro==0 && modos!=19)
               errores(15);
            if(val_operando==1 && modos!=19)
               errores(16);
            if(val_operando==2 && modos!=19)
               errores(17);
            if(modos==0)
               operando_igual=2;
            if(val_operando==0 && operando_igual==1 && modos!=19)
               a_temporal();
            if(operando_igual==0 && encontro==1 && modos!=19)
               errores(18);
            tipo_codop=5;
         }  //Fin del si se abrio correctamente el TABOP.txt (End of the "if the TABOP.txt open correctly")
         else
            perror("\n\nError");
         close(fd);
         fd=open(archivo,0);
         lseek(fd,posicion,0);
      }  //Fin de la función busqueda_tabop (End of the function busqueda_tabop)

/**************************************************************Id_operando******************************************************************/

      void id_operando()
      {
         int incorrecto,i,aux,aux2,aux3,senal,senal_auto,signo,coma;
         char letra;
         incorrecto=0;  //Indica si el operando es correcto, 0=si 1=no (Indicates if the operand is correct, 0=yes 1=no)
         modos=0;  /*Resetea la variable que indica el modo que se encontro: 0=equivocado, 1=Inherente, 2=Inmediato de 8 bits, 3=Inmediato de
         16 bits, 4=Directo, 5=Extendido, 6=Indizado de 5 bits (IDX), 7=Indizado de 9 bits (IDX1), 8=Indizado de 16 bits (IDX2), 9=Indizado
         indirecto de 16 bits ([IDX2]), 10=Indizado de auto pre decremento (IDX), 11=Indizado de auto post decremento (IDX), 12=Indizado de
         auto pre incremento (IDX), 13=Indizado de auto post incremento (IDX), 14=Indizado de acumulador (IDX), 15=Indizado de acumulador
         indirecto ([D,IDX]), 17=Relativo (REL) de 8 bits o incluso el extendido cuando es etiqueta, 16=Relativo (REL) de 16 bits,
         18=Podria ser exacto o relativo dependiendo de lo que indique el TABOP.txt, 19=Extendido cuando es etiqueta */

         //-----------------------------Inherente---------------------------------------
         //------------------------------Inherent---------------------------------------

         if(strcmp(operando,"null")==0)  //Direccionamiento Inherente (Inherent addressing)
         {
            modos=1;
            busqueda_tabop();
            return;
         }

         //-----------------------------Inmediato---------------------------------------
         //-----------------------------Immediate---------------------------------------

//-----------------------------------------------------Si el operando empieza con #-----------------------------------------------------------
//-----------------------------------------------------If the operand start whith #-----------------------------------------------------------

         if(operando[0]=='#')  //Direccionamiento Inmediato (Immediate addressing)
         {
            if(operando[1]=='\0')
            {
               errores(19);
               incorrecto=1;
            }
            else
            {
               if(operando[1]=='$')  //Hexadecimal
               {
                  if(operando[2]=='\0')
                  {
                     errores(20);
                     incorrecto=1;
                   }
                  else
                  {
                     i=2;
                     while(operando[i]!='\0')
                     {
                        if(operando[i]!='0' && operando[i]!='1' && operando[i]!='2' && operando[i]!='3' && operando[i]!='4' && operando[i]!='5' && operando[i]!='6' && operando[i]!='7' && operando[i]!='8' && operando[i]!='9' && operando[i]!='a' && operando[i]!='b' && operando[i]!='c' && operando[i]!='d' && operando[i]!='e' && operando[i]!='f' && operando[i]!='A' && operando[i]!='B' && operando[i]!='C' && operando[i]!='D' && operando[i]!='E' && operando[i]!='F')
                        {
                           incorrecto=1;
                           errores(21);
                        }
                        i++;
                     }
                  }
               }
               else if(operando[1]=='@')  //Octal
               {
                  if(operando[2]=='\0')
                  {
                     errores(22);
                     incorrecto=1;
                  }
                  else
                  {
                     i=2;
                     while(operando[i]!='\0')
                     {
                        if(operando[i]!='0' && operando[i]!='1' && operando[i]!='2' && operando[i]!='3' && operando[i]!='4' && operando[i]!='5' && operando[i]!='6' && operando[i]!='7')
                        {
                           incorrecto=1;
                           errores(23);
                        }
                        i++;
                     }
                  }
               }
               else if(operando[1]=='%')  //Binario (Binary)
               {
                  if(operando[2]=='\0')
                  {
                     errores(24);
                     incorrecto=1;
                  }
                  else
                  {
                     i=2;
                     while(operando[i]!='\0')
                     {
                        if(operando[i]!='0' && operando[i]!='1')
                        {
                           incorrecto=1;
                           errores(25);
                        }
                        i++;
                     }
                  }
               }
               else if(operando[1]=='0' || operando[1]=='1' || operando[1]=='2' || operando[1]=='3' || operando[1]=='4' || operando[1]=='5' || operando[1]=='6' || operando[1]=='7' || operando[1]=='8' || operando[1]=='9')  //Decimal
               {
                  i=2;
                  while(operando[i]!='\0')
                  {
                     if(operando[i]!='0' && operando[i]!='1' && operando[i]!='2' && operando[i]!='3' && operando[i]!='4' && operando[i]!='5' && operando[i]!='6' && operando[i]!='7' && operando[i]!='8' && operando[i]!='9')
                     {
                        incorrecto=1;
                        errores(26);
                     }
                     i++;
                  }
               }
               else
                  errores(27);
            }
            if(incorrecto==0)  //Si esta correcto, no importando en cual base númerica, con que este bien en una (If it´s correct, no matter in which numeric base, we only need to be ok in one)
            {
               if(extraer(operando,-1)>=0 && extraer(operando,-1)<=255)
               {
                  modos=2;  //Inmediato de 8 bits (Immediate of 8 bits)
                  busqueda_tabop();
               }
               else if(extraer(operando,-1)>=256 && extraer(operando,-1)<=65535)
               {
                  modos=3;  //Inmediato de 16 bits (Immediate of 16 bits)
                  busqueda_tabop();
               }
               else
                  errores(28);
            }
            return;
         }  //Fin del direccionamiento inmediato (End of the immediate addressing)

         //-----------------------------Directo, extendido, indizados o de incremento/decremento---------------------------------------
         //---------------------------------Direct, extended, indexed or increment/decrement-------------------------------------------

//---------------------------------------------------Si el operando empieza con $------------------------------------------------------------
//---------------------------------------------------If the operand starts with $------------------------------------------------------------

         else if(operando[0]=='$')  //Hexadecimal, directo o extendido (Hexadecimal, direct or extended)
         {
            if(operando[1]=='\0')
            {
               errores(29);
               incorrecto=1;
            }
            else
            {
               i=1;
               while(operando[i]!='\0')
               {
                  if(operando[i]!='0' && operando[i]!='1' && operando[i]!='2' && operando[i]!='3' && operando[i]!='4' && operando[i]!='5' && operando[i]!='6' && operando[i]!='7' && operando[i]!='8' && operando[i]!='9' && operando[i]!='a' && operando[i]!='b' && operando[i]!='c' && operando[i]!='d' && operando[i]!='e' && operando[i]!='f' && operando[i]!='A' && operando[i]!='B' && operando[i]!='C' && operando[i]!='D' && operando[i]!='E' && operando[i]!='F')
                  {
                     incorrecto=1;
                     errores(30);
                  }
                  i++;
               }
            }
            if(incorrecto==0)  //Si entro al ciclo y todos los caracteres son validos (If it enter the cycle and all the characters are correct)
            {
               if(extraer(operando,-1)>=0 && extraer(operando,-1)<=255)  //Direccionamiento directo (Direct addressing)
               {
                  modos=4;  //Modo directo (Direct mode)
                  busqueda_tabop();
               }
               else if(extraer(operando,-1)>=256 && extraer(operando,-1)<=65535)  //Direccionamiento extendido (Extended addressing)
               {
                  modos=5;  //Modo extendido (Extended mode)
                  busqueda_tabop();
               }
               else
                  errores(31);
            }
            return;
         }

//----------------------------------------------------Si el operando empieza con @-----------------------------------------------------------
//----------------------------------------------------If the operand starts with @-----------------------------------------------------------

         else if(operando[0]=='@')  //Octal, directo o extendido (Octal, direct or extended)
         {
            if(operando[1]=='\0')
            {
               errores(32);
               incorrecto=1;
            }
            else
            {
               i=1;
               while(operando[i]!='\0')
               {
                  if(operando[i]!='0' && operando[i]!='1' && operando[i]!='2' && operando[i]!='3' && operando[i]!='4' && operando[i]!='5' && operando[i]!='6' && operando[i]!='7')
                  {
                     incorrecto=1;
                     errores(33);
                  }
                  i++;
               }
            }
            if(incorrecto==0)
            {
               if(extraer(operando,-1)>=0 && extraer(operando,-1)<=255)  //Direccionamiento directo (Direct addressing)
               {
                  modos=4;  //Modo directo (Direct mode)
                  busqueda_tabop();
               }
               else if(extraer(operando,-1)>=256 && extraer(operando,-1)<=65535)  //Direccionamiento extendido (Extended addressing)
               {
                  modos=5;  //Modo extendido (Extended mode)
                  busqueda_tabop();
               }
               else
                  errores(34);
            }
            return;
         }

//--------------------------------------------------Si el operando empieza con %-------------------------------------------------------------
//--------------------------------------------------If the operand starts with %-------------------------------------------------------------

         else if(operando[0]=='%')  //Binario, directo o extendido (Binary, direct or extended)
         {
            if(operando[1]=='\0')
            {
               errores(35);
               incorrecto=1;
            }
            else
            {
               i=1;
               while(operando[i]!='\0')
               {
                  if(operando[i]!='0' && operando[i]!='1')
                  {
                     incorrecto=1;
                     errores(36);
                  }
                  i++;
               }
            }
            if(incorrecto==0)
            {
               if(extraer(operando,-1)>=0 && extraer(operando,-1)<=255)  //Direccionamiento directo (Direct addressing)
               {
                  modos=4;  //Modo directo (Direct mode)
                  busqueda_tabop();
               }
               else if(extraer(operando,-1)>=256 && extraer(operando,-1)<=65535)  //Direccionamiento extendido (Extended addressing)
               {
                  modos=5;  //Modo extendido (Extended mode)
                  busqueda_tabop();
               }
               else
                  errores(37);
            }
            return;
         }

//------------------------------------------------Si el operando empieza con 0 ... 9----------------------------------------------------------
//------------------------------------------------If the operand starts with 0 ... 9----------------------------------------------------------

         else if(operando[0]=='0' || operando[0]=='1' || operando[0]=='2' || operando[0]=='3' || operando[0]=='4' || operando[0]=='5' || operando[0]=='6' || operando[0]=='7' || operando[0]=='8' || operando[0]=='9' || operando[0]=='-')  //Decimal, directo, extendido, indizados o incremento/decremento
         {
            signo=0;  //Indica si hay signo lo que elimina la posibilidad de que sea directo, extendido, indizado de 16 bits y los de incremento/decremento 0=no 1=si (If there is a signe which delete the posibility that it can be direct, extended, indexed of 16 bits and those of the pre/post increment/decrement, 0=no 1=yes)
            coma=0;
            aux=0;  //Si despues del signo de menos (en la primera parte)esta la coma, 0=no 1=si (If after the signe are the comma, 0=no 1=yes)
            aux2=0;  //Si despues del signo de menos esta el fin de archivo, 0=no 1=si (If after the signe are the end of file, 0=no 1=yes)
            aux3=0;  //Si despues de la coma esta el fin de archivo, 0=no 1=si (If after the comma are the come, 0=no 1=yes)
            if(operando[0]=='-')
            {
               signo=1;
               i=1;
            }
            else
               i=0;
            while(operando[i]!='\0' && coma==0)//Valida que sean numeros (si ahy, hasta la coma) y deja el valor de i despues de la coma, si ahy (Validate that they be numbers and it leave the value after the comma)
            {
               if(operando[i]==',')
                  coma=1;
               if(operando[i]!='0' && operando[i]!='1' && operando[i]!='2' && operando[i]!='3' && operando[i]!='4' && operando[i]!='5' && operando[i]!='6' && operando[i]!='7' && operando[i]!='8' && operando[i]!='9' && operando[i]!=',')
               {
                  incorrecto=1;
                  if(operando[i]=='-')  //Más de un signo es un error (More than a signe it´s a error)
                     signo++;
                  else
                     errores(38);
               }
               i++;
            }
            if(operando[0]=='-' && operando[1]==',')  //Si despues del signo esta la coma (If after the signe are the comma)
               aux=1;
            if(operando[0]=='-' && operando[1]=='\0')  //Si despues del signo esta el fin de cadena (If after the signe are the end of string)
               aux2=1;
            if(coma==1 && operando[i]=='\0')  //Si despues de la coma esta el fin de cadena (If after the comma are the end of string)
               aux3=1;
            if(aux==1)
               errores(39);
            if(aux2==1)
               errores(40);
            if(aux3==1)
               errores(41);
            if(signo>1)
               errores(42);
            if(incorrecto==0 && aux==0 && aux2==0 && aux3==0 && signo<=1)  //Si esta correcta la primer parte (antes de la coma, si es que ahy) (If the first part is correct)
            {
               if(coma==0)  //Si es directo o extendido, osea que no ahy coma (If it´s direct or extended, there´s no comma)
               {
                  if(extraer(operando,-1)>=0 && extraer(operando,-1)<=255)  //Direccionamiento directo (Direct addressing)
                  {
                     modos=4;  //Direccionamiento directo (Direct address)
                     busqueda_tabop();
                  }
                  else if(extraer(operando,-1)>=256 && extraer(operando,-1)<=65535)  //Direccionamiento extendido (extended addressing)
                  {
                     modos=5;  //Direccionamiento extendido (extended addressing)
                     busqueda_tabop();
                  }
                  else
                     errores(43);
               }
               else if(coma==1)  //Validaciones si es que tiene coma, para los indizados (Validations if it´s comma, for the indexes)
               {
                  incorrecto=1;
                  senal=0;  //Indica si se encontro el modo indizado de 5, 9 ó 16 bits, 0=no 1=si (Indicates if we found the indexed mode of 5, 9 or 16 bits, 0=no 1=yes)
                  senal_auto=0;  //Indica si se encontro el modo indizado auto pre/post decremento/incremento, 0=no 1=si (Indicates if we found the indexed of auto pre/post increment/decrement, 0=no 1=yes)
                  if(operando[i]=='x' || operando[i]=='X' || operando[i]=='y' || operando[i]=='Y')
                  {
                     if(operando[i+1]=='\0')  //Indizado de 5,9 ó 16 bits (Indexed of 5, 9 or 16 bits)
                     {
                        incorrecto=0;
                        senal=1;
                     }
                     if(operando[i+1]=='-')  //Modo de post decremento (Post decrement mode)
                        if(operando[i+2]=='\0')
                        {
                           modos=11;  //Modo de post decremento (Post decrement mode)
                           incorrecto=0;
                           senal_auto=1;
                        }
                     if(operando[i+1]=='+')  //Modo de post incremento (Post increment mode)
                        if(operando[i+2]=='\0')
                        {
                           modos=13;  //Modo de post incremento (post increment mode)
                           incorrecto=0;
                           senal_auto=1;
                        }
                  }
                  if(operando[i]=='s' || operando[i]=='S')
                     if(operando[i+1]=='p' || operando[i+1]=='P')  //Indizado de 5,9 ó 16 bits (Indexed of 5, 9 or 16 bits)
                     {
                        if(operando[i+2]=='\0')
                        {
                           incorrecto=0;
                           senal=1;
                        }
                        if(operando[i+2]=='-')  //Modo de post decremento (post decrement mode)
                           if(operando[i+3]=='\0')
                           {
                              modos=11;  //Modo de post decremento (post decrement mode)
                              incorrecto=0;
                              senal_auto=1;
                           }
                        if(operando[i+2]=='+')  //Modo de post incremento (post increment mode)
                           if(operando[i+3]=='\0')
                           {
                              modos=13;  //Modo de post incremento (post increment mode)
                              incorrecto=0;
                              senal_auto=1;
                           }
                     }
                  if(operando[i]=='p' || operando[i]=='P')  //Indizado de 5,9 ó 16 bits
                     if(operando[i+1]=='c' || operando[i+1]=='C')
                        if(operando[i+2]=='\0')
                        {
                           incorrecto=0;
                           senal=1;
                        }
                  if(operando[i]=='-')  //Modo de pre decremento (pre decrement mode)
                  {
                     if(operando[i+1]=='x' || operando[i+1]=='X' || operando[i+1]=='y' || operando[i+1]=='Y')
                        if(operando[i+2]=='\0')
                        {
                           modos=10;  //Modo de pre decremento (pre decrement mode)
                           incorrecto=0;
                           senal_auto=1;
                        }
                     if(operando[i+1]=='s' || operando[i+1]=='S')
                        if(operando[i+2]=='p' || operando[i+2]=='P')
                           if(operando[i+3]=='\0')
                           {
                              modos=10;  //Modo de pre decremento (pre decrement mode)
                              incorrecto=0;
                              senal_auto=1;
                           }
                  }
                  if(operando[i]=='+')  //Modo de pre incremento (pre decrement mode)
                  {
                     if(operando[i+1]=='x' || operando[i+1]=='X' || operando[i+1]=='y' || operando[i+1]=='Y')
                        if(operando[i+2]=='\0')
                        {
                           modos=12;  //Modo de pre incremento (pre increment mode)
                           incorrecto=0;
                           senal_auto=1;
                        }
                     if(operando[i+1]=='s' || operando[i+1]=='S')
                        if(operando[i+2]=='p' || operando[i+2]=='P')
                           if(operando[i+3]=='\0')
                           {
                              modos=12;  //Modo de pre incremento (pre increment mode)
                              incorrecto=0;
                              senal_auto=1;
                           }
                  }
                  if(incorrecto==1)
                     errores(44);
                  else if(incorrecto==0)  //Si encontro alguno de los modos, ahorita sin importar cual (If we found any of the modes, no matter which one)
                  {
                     if(senal==1)  //Si encontro un indizado (If we found a indexed)
                     {
                        if(extraer(operando,-1)>=-16 && extraer(operando,-1)<=15)  //Indizado de 5 bits (Indexed of 5 bits)
                        {
                           modos=6;  //Indizado de 5 bits (Indexed of 5 bits)
                           busqueda_tabop();
                        }
                        else if(extraer(operando,-1)>=-256 && extraer(operando,-1)<=-17)  //Indizado de 9 bits (Indexed of 9 bits)
                        {
                           modos=7;  //Indizado de 9 bits (Indexed of 9 bits)
                           busqueda_tabop();
                        }
                        else if(extraer(operando,-1)>=16 && extraer(operando,-1)<=255)  //Indizado de 9 bits (Indexed of 9 bits)
                        {
                           modos=7;  //Indizado de 9 bits (Indexed of 9 bits)
                           busqueda_tabop();
                        }
                        else if(extraer(operando,-1)>=256 && extraer(operando,-1)<=65535)  //Indizado de 16 bits (Indexed of 16 bits)
                        {
                           modos=8;  //Indizado de 16 bits (Indexed of 16 bits)
                           busqueda_tabop();
                        }
                        else
                           errores(45);
                     }
                     if(senal_auto==1)  //Si encontro uno de los de auto pre/post incremento/decremento (If it found one of the auto pre/post increment/decrement)
                     {
                        if(extraer(operando,-1)>=1 && extraer(operando,-1)<=8)
                        {
                           busqueda_tabop();
                        }
                        else
                           errores(46);
                     }
                  }   //Fin del, si se encontro alguno de los modos (End of the "if it found one of the modes")
               }  //Fin del, si habia coma (End of the "if it have comma")
            }  //Fin del si al salir del ciclo el operando esta correcto (End of the "if when exit the cycle the operand is correct")
            return;
         }  //Fin del decimal

         //-----------------------------Indizado indirecto de 16 bits o de acumulador indirecto---------------------------------------
         //------------------------------Indexed indirect of 16 bits or of acumulator indirect----------------------------------------

//--------------------------------------------------Si el operando empieza con corchete-------------------------------------------------------
//----------------------------------------------If the operand starts with a square braket----------------------------------------------------

         else if(operando[0]=='[')
         {
            incorrecto=1;
            if(operando[1]=='\0')
               errores(47);
            if(operando[1]==',')
               errores(48);
            if(operando[1]==']')
               errores(49);
            if(operando[1]=='d' || operando[1]=='D')  //Si es de acumulador indirecto (If it´s of the acumulator indirect)
            {
               if(operando[2]==',')
               {
                  if(operando[3]=='x' || operando[3]=='X' || operando[3]=='y' || operando[3]=='Y')
                  {
                     if(operando[4]==']')
                        if(operando[5]=='\0')  //Indizado de acumulador indirecto (Indexed of acumulator indirect)
                           incorrecto=0;
                     else
                        errores(50);
                  }
                  if(operando[3]=='s' || operando[3]=='S')
                     if(operando[4]=='p' || operando[4]=='P')  //Indizado de acumulador indirecto (Indexed of acumulator indirect)
                     {
                        if(operando[5]==']')
                           if(operando[6]=='\0')
                              incorrecto=0;
                        else
                           errores(51);
                     }
                  if(operando[3]=='p' || operando[3]=='P')  //Indizado de acumulador indirecto (Indexed of acumulator indirect)
                     if(operando[4]=='c' || operando[4]=='C')
                     {
                        if(operando[5]==']')
                           if(operando[6]=='\0')
                              incorrecto=0;
                        else
                           errores(52);
                     }
               }
               i=0;
               aux=0;
               coma=0;
               while(operando[i]!='\0')
               {
                  if(operando[i]==']')
                     aux=1;
                  if(operando[i]==',')
                     coma=1;
                  i++;
               }
               if(aux==0)
                  errores(53);
               if(coma==0)
                  errores(54);
               if(incorrecto==0)
               {
                  modos=15;  //Indizado de acumulador indirecto (Indexed of acumulator indirect)
                  busqueda_tabop();
               }
               else
                  errores(55);
            }
            if(operando[1]=='0' || operando[1]=='1' || operando[1]=='2' || operando[1]=='3' || operando[1]=='4' || operando[1]=='5' || operando[1]=='6' || operando[1]=='7' || operando[1]=='8' || operando[1]=='9' || operando[1]=='-')  //Si es indizado indirecto de 16 bits
            {
               signo=0;
               incorrecto=0;
               coma=0;
               aux2=0;
               if(operando[1]=='-')
               {
                  signo=1;
                  i=2;
               }
               else
                  i=1;
               while(operando[i]!='\0' && coma==0)  //Valida hasta la coma (Validate until the comma)
               {
                  if(operando[i]==']')
                     aux2=1;
                  if(operando[i]==',')
                     coma=1;
                  if(operando[i]!='0' && operando[i]!='1' && operando[i]!='2' && operando[i]!='3' && operando[i]!='4' && operando[i]!='5' && operando[i]!='6' && operando[i]!='7' && operando[i]!='8' && operando[i]!='9' && operando[i]!=',')
                  {
                     if(operando[i]=='-')  //Más de un signo es un error (More than a signe is a error)
                        signo++;
                     else
                        errores(56);
                     incorrecto=1;
                  }
                  i++;
               }
               if(signo>1)
                  errores(57);
               if(coma==1 && incorrecto==0 && signo<=1)  //Si esta correcto, con coma (If it´s correct, whith comma)
               {
                  incorrecto=1;
                  if(operando[i]=='x' || operando[i]=='X' || operando[i]=='y' || operando[i]=='Y')  //Indizado indirecto de 16 bits (Indexed indirect of 16 bits)
                     if(operando[i+1]==']')
                        if(operando[i+2]=='\0')  //Indizado indirecto de 16 bits (Indexed indirect of 16 bits)
                           incorrecto=0;
                  if(operando[i]=='s' || operando[i]=='S')
                     if(operando[i+1]=='p' || operando[i+1]=='P')  //Indizado indirecto de 16 bits (Indexed indirect of 16 bits)
                        if(operando[i+2]==']')
                           if(operando[i+3]=='\0')
                              incorrecto=0;
                  if(operando[i]=='p' || operando[i]=='P')  //Indizado indirecto de 16 bits (Indexed indirect of 16 bits)
                     if(operando[i+1]=='c' || operando[i+1]=='C')
                        if(operando[i+2]==']')
                           if(operando[i+3]=='\0')
                              incorrecto=0;
                  if(incorrecto==1)
                     errores(58);
                  i=0;
                  aux=0;
                  while(operando[i]!='\0')
                  {
                     if(operando[i]==']')
                        aux=1;
                     i++;
                  }
                  if(aux==0)
                     errores(59);
                  //if(incorrecto==0)
                  //{
                     if(extraer(operando,-1)>=0 && extraer(operando,-1)<=65535)
                     {
                        if(incorrecto==0)
                        {
                        modos=9;  //Indizado indirecto de 16 bits (Indexed indirect of 16 bits)
                        busqueda_tabop();
                        }
                     }
                     else
                        errores(60);
                  //}
               }  //Fin del, si hasta la coma estaba bien (End of the "if until the comma it´s ok")
               if(coma==0)
                  errores(61);
               if(coma==0 && aux2==0)
                  errores(62);
            }
            if(operando[1]!='d' && operando[1]!='D' && operando[1]!=']' && operando[1]!='0' && operando[1]!='1' && operando[1]!='2' && operando[1]!='3' && operando[1]!='4' && operando[1]!='5' && operando[1]!='6' && operando[1]!='7' && operando[1]!='8' && operando[1]!='9' && operando[1]!='-' && operando[1]!='\0' && operando[1]!=',')
            {
               errores(63);
               i=0;
               aux=0;
               coma=0;
               while(operando[i]!='\0')
               {
                  if(operando[i]==']')
                     aux=1;
                  if(operando[i]==',')
                     coma=1;
                  i++;
               }
               if(aux==0)
                  errores(64);
               if(coma==0)
                  errores(65);
            }
            return;
         }  //Fin del Indizado indirecto de 16 bits o de acumulador indirecto (End of the indexed indirect of 16 bits or the acumulator indirect)

         //-----------------------------Extendido, indizado de acumulador o relativo de 8 y 16 bits---------------------------------------
         //----------------------------Extended, indexed of acumulator or relative of 8 and 16 bits---------------------------------------

//--------------------------------------------------Si el operando empieza con a ... z--------------------------------------------------------
//--------------------------------------------------If the operand starts with a ... z--------------------------------------------------------

         else if(operando[0]=='a' || operando[0]=='b' || operando[0]=='c' || operando[0]=='d' || operando[0]=='e' || operando[0]=='f' || operando[0]=='g' || operando[0]=='h' || operando[0]=='i' || operando[0]=='j' || operando[0]=='k' || operando[0]=='l' || operando[0]=='m' || operando[0]=='n' || operando[0]=='ñ' || operando[0]=='o' || operando[0]=='p' || operando[0]=='q' || operando[0]=='r' || operando[0]=='s' || operando[0]=='t' || operando[0]=='u' || operando[0]=='v' || operando[0]=='w' || operando[0]=='x' || operando[0]=='y' || operando[0]=='z' || operando[0]=='A' || operando[0]=='B' || operando[0]=='C' || operando[0]=='D' || operando[0]=='E' || operando[0]=='F' || operando[0]=='G' || operando[0]=='H' || operando[0]=='I' || operando[0]=='J' || operando[0]=='K' || operando[0]=='L' || operando[0]=='M' || operando[0]=='N' || operando[0]=='Ñ' || operando[0]=='O' || operando[0]=='P' || operando[0]=='Q' || operando[0]=='R' || operando[0]=='S' || operando[0]=='T' || operando[0]=='U' || operando[0]=='V' || operando[0]=='W' || operando[0]=='X' || operando[0]=='Y' || operando[0]=='Z')  //Modo extendido, indizado de acumulador o relativo de 8 y 16 bits
         {
            i=0;
            coma=0;
            while(operando[i]!='\0' && coma==0)  //Para checar si tiene comas (To check if it has commas)
            {
               if(operando[i]==',')
                  coma=1;
               i++;
            }
            if(coma==1)
            {
               incorrecto=1;
               if(operando[0]=='a' || operando[0]=='A'  || operando[0]=='b'  || operando[0]=='B'  || operando[0]=='d'  || operando[0]=='D')
                  if(operando[1]==',')
                  {
                     if(operando[2]=='x' || operando[2]=='X' || operando[2]=='y' || operando[2]=='Y')
                        if(operando[3]=='\0')
                           incorrecto=0;
                     if(operando[2]=='s' || operando[2]=='S')
                        if(operando[3]=='p' || operando[3]=='P')  //Indizado de acumulador (Indexed of acumulator)
                           if(operando[4]=='\0')
                              incorrecto=0;
                     if(operando[2]=='p' || operando[2]=='P')  //Indizado de acumulador (Indexed of acumulator)
                        if(operando[3]=='c' || operando[3]=='C')
                           if(operando[4]=='\0')
                              incorrecto=0;
                  }
               if(incorrecto==0)
               {
                  modos=14;  //Indizado de acumulador (Indexed of acumulator)
                  busqueda_tabop();
               }
               else if(incorrecto==1)
                  errores(66);
            }
            else if(coma==0)
            {
               i=0;
               aux=0;  //Indica si la primer letra se repite, 0=no 1=si (Check if the first character repeat, 0=no 1=yes)
               aux2=0;  //Indica si ahy un caracter invalido, 0=no 1=si (Check if there is a invalid character, 0=no 1=yes)
               aux3=0;
               while(operando[i]!='\0')
               {
                  /*if(operando[i]=='0'  || operando[i]=='1'  || operando[i]=='2'  || operando[i]=='3'  || operando[i]=='4'  || operando[i]=='5'  || operando[i]=='6'  || operando[i]=='7'  || operando[i]=='8'  || operando[i]=='9')
                     aux3=1;*/
                  if(i!=0)
                     if(operando[0]==operando[i])
                        aux=1;
                  if(operando[i]!='a' && operando[i]!='b' && operando[i]!='c' && operando[i]!='d' && operando[i]!='e' && operando[i]!='f' && operando[i]!='g' && operando[i]!='h' && operando[i]!='i' && operando[i]!='j' && operando[i]!='k' && operando[i]!='l' && operando[i]!='m' && operando[i]!='n' && operando[i]!='ñ' && operando[i]!='o' && operando[i]!='p' && operando[i]!='q' && operando[i]!='r' && operando[i]!='s' && operando[i]!='t' && operando[i]!='u' && operando[i]!='v' && operando[i]!='w' && operando[i]!='x' && operando[i]!='y' && operando[i]!='z' && operando[i]!='A' && operando[i]!='B' && operando[i]!='C' && operando[i]!='D' && operando[i]!='E' && operando[i]!='F' && operando[i]!='G' && operando[i]!='H' && operando[i]!='I' && operando[i]!='J' && operando[i]!='K' && operando[i]!='L' && operando[i]!='M' && operando[i]!='N' && operando[i]!='Ñ' && operando[i]!='O' && operando[i]!='P' && operando[i]!='Q' && operando[i]!='R' && operando[i]!='S' && operando[i]!='T' && operando[i]!='U' && operando[i]!='V' && operando[i]!='W' && operando[i]!='X' && operando[i]!='Y' && operando[i]!='Z' && operando[i]!='0'  && operando[i]!='1'  && operando[i]!='2'  && operando[i]!='3'  && operando[i]!='4'  && operando[i]!='5'  && operando[i]!='6'  && operando[i]!='7'  && operando[i]!='8'  && operando[i]!='9'  && operando[i]!='_' )
                  {
                     errores(67);
                     aux2=1;
                  }
                  i++;
               }
               if(aux==1)
                  errores(68);
               if(i>7)
                  errores(69);
               if(aux==0 && aux2==0 && i<=7)
               {
                  if(aux3==0)  //Podría ser extendido o relativo (It could be extended or relative)
                  {
                     modos=18;  //Extendido o relativo (Extended or relative)
                     busqueda_tabop();
                     if(modos==19)
                     {
                        modos=5;
                        busqueda_tabop();
                     }
                  }
                  else if(aux3==1)  //Es modo extendido (Is extended mode)
                  {
                     modos=5;  //Modo extendido (Extended mode)
                     busqueda_tabop();
                  }
               }
            }  //Fin del si no hubo coma (End of the "if it doesn´t exist a comma")
            else
               errores(70);
            return;
         }  //Fin del modo extendido, indizado de acumulador o relativo de 8 y 16 bits (En of the extended mode, indexed of acumulator or relative of 8 and 16 bits)

         //-----------------------------Modo indizado de 5 bits---------------------------------------
         //------------------------------Indexed mode of 5 bits---------------------------------------

//----------------------------------------------------Si el operando empieza con coma---------------------------------------------------------
//--------------------------------------------------If the operand starts with a comma--------------------------------------------------------

         else if(operando[0]==',')
         {
            incorrecto=1;
            if(operando[1]=='x' || operando[1]=='X' || operando[1]=='y' || operando[1]=='Y')
               if(operando[2]=='\0')
                  incorrecto=0;
            if(operando[1]=='s' || operando[1]=='S')
               if(operando[2]=='p' || operando[2]=='P')  //Indizado de 5 bits (Indexed of 5 bits)
                  if(operando[3]=='\0')
                     incorrecto=0;
            if(operando[1]=='p' || operando[1]=='P')  //Indizado de 5 bits (Indexed of 5 bits)
               if(operando[2]=='c' || operando[2]=='C')
                  if(operando[3]=='\0')
                     incorrecto=0;
            if(incorrecto==0)
            {
               modos=6;
               busqueda_tabop();
            }
            else if(incorrecto==1)
            {
               if(operando[1]=='\0')
               {
                  errores(71);
                  return;
               }
               errores(72);
            }
            return;
         }  //Fin del modo indizado de 5 bits (End of the indexed mode of 5 bits)

//----------------------------------------------------Cualquier otra cosa---------------------------------------------------------
//--------------------------------------------------------Anything else-----------------------------------------------------------

         errores(73);  //Si el operando empieza con cualquier otra cosa entonces que se regrese (If the operand starts with something else then it has to return)
         modos=0;
         busqueda_tabop();
         return;
      }  //Fin de la función id_operando (End of the function id_operando)

/***************************************************************Potencia********************************************************************/

      int potencia(int numero,int potencia)
      {
         int aux,j;
         aux=numero;
         if(potencia!=0)
            for(j=1;j<potencia;j++)
               numero=aux*numero;
         else if(potencia==0)
            numero=1;
         return numero;
      }  //Fin de la función potencia (End of the function potencia)

/*****************************************************************Extraer*******************************************************************/

      int extraer(char cadena[],int opcional)
      {
         int longitud,base,subindice,listo,i,resultado,signo;
         char cadena_aux[50];
         i=0;
         listo=0;  //Indica que ya se encontro el simbolo que identifica la base, 0=no 1=si (Indicates that we found the symbol that identifycates the base, 0=no 1=yes)
         signo=0;  //Indica que el número decimal tiene signo, 0=no 1=si (Indicates that the decimal number has a signe, 0=no 1=yes)
         while(cadena[i]!='\0' && listo==0)
         {
            if(cadena[i]=='$')
            {
               base=16;
               listo=1;
               subindice=i;  //Guarda la posición del comienzo del número (que seria: subindice + 1 depues del indicador de base, si hay) (Store the position from the start of the number)
               subindice++;
            }
            else if(cadena[i]=='0' || cadena[i]=='1' || cadena[i]=='2' || cadena[i]=='3' || cadena[i]=='4' || cadena[i]=='5' || cadena[i]=='6' || cadena[i]=='7' || cadena[i]=='8' || cadena[i]=='9' || cadena[i]=='-')
            {
               base=10;
               listo=1;
               subindice=i;
               if(cadena[i]=='-')
                  signo=1;
            }
            else if(cadena[i]=='@')
            {
               base=8;
               listo=1;
               subindice=i;
               subindice++;
            }
            else if(cadena[i]=='%')
            {
               base=2;
               listo=1;
               subindice=i;
               subindice++;
            }
            i++;
         }
         if(opcional>0)
         {
            if(opcional==16)
            {
               base=16;
               subindice=0;
            }
            else if(opcional==8)
            {
               base=8;
               subindice=0;
            }
            else if(opcional==2)
            {
               base=2;
               subindice=0;
            }
         }
         if(listo==1)
         {
            i=0;
            while(cadena[subindice]=='0' || cadena[subindice]=='1' || cadena[subindice]=='2' || cadena[subindice]=='3' || cadena[subindice]=='4' || cadena[subindice]=='5' || cadena[subindice]=='6' || cadena[subindice]=='7' || cadena[subindice]=='8' || cadena[subindice]=='9' || cadena[subindice]=='a' || cadena[subindice]=='b' || cadena[subindice]=='c' || cadena[subindice]=='d' || cadena[subindice]=='e' || cadena[subindice]=='f' || cadena[subindice]=='A' || cadena[subindice]=='B' || cadena[subindice]=='C' || cadena[subindice]=='D' || cadena[subindice]=='E' || cadena[subindice]=='F' || cadena[subindice]=='-' && cadena[subindice]!='\0' && cadena[subindice]!=',')
            {
               cadena_aux[i]=cadena[subindice];
               i++;
               subindice++;
            }
            cadena_aux[i]='\0';
            longitud=strlen(cadena_aux);
            i=0;
            resultado=0;
            longitud--;  //Para no contar el fin de cadena (For not count the end of string)
            while(longitud>=0)
            {
               if(cadena_aux[longitud]==45)  //Si es un signo de menos (If it´s a signe)
               {}
               else if(cadena_aux[longitud]>=48 && cadena_aux[longitud]<=57)  //Entre 0 ... 9 tienen ASCII de 48 ... 57 (Between 0 ... 9  has ASCII from 48 ... 57)
                  resultado=resultado+((cadena_aux[longitud]-48)*(potencia(base,i)));  //(ASCII - 48) (base ^ posición del número)
               else if(cadena_aux[longitud]>=65 && cadena_aux[longitud]<=70)  //Entre A ... F tienen ASCII de 65 ... 70 (Between A ... F  has ASCII from 65 ... 70)
                  resultado=resultado+((cadena_aux[longitud]-55)*(potencia(base,i)));  //(ASCII - 55) (base ^ posición del número)
               else if(cadena_aux[longitud]>=97 && cadena_aux[longitud]<=102)  //Entre a ... f tienen ASCII de 97 ... 102 (Between a ... f  has ASCII from 97 ... 102)
                  resultado=resultado+((cadena_aux[longitud]-87)*(potencia(base,i)));  //(ASCII - 87) (base ^ posición del número)
               longitud--;
               i++;
            }
            if(signo==1)
               resultado=resultado*-1;
         }
            return resultado;
      }  //Fin de la función extraer (End of the function extraer)

/***************************************************************Directivas******************************************************************/

      int directivas()
      {
         int aux,correcto,i,signo;

         tipo=0;  /*Indica de que directiva se trata, 1=ORG, 2=END, 3=DW, 4=DB, 5=DC.W, 6= DC.B, 7=FCB, 8=FDB, 9=FCC, 10=DS,
         11=DS.B, 12=DS.W, 13=RMB, 14=RMW, 15=EQU*/
         tipo_codop=1;  //Directiva

         if(strcmp("ORG",codop)==0 || strcmp("org",codop)==0 || strcmp("Org",codop)==0 || strcmp("oRg",codop)==0 || strcmp("orG",codop)==0 || strcmp("OrG",codop)==0 || strcmp("oRG",codop)==0 || strcmp("ORg",codop)==0)
            tipo=1;
         if(strcmp("END",codop)==0 || strcmp("end",codop)==0 || strcmp("End",codop)==0 || strcmp("eNd",codop)==0 || strcmp("enD",codop)==0 || strcmp("EnD",codop)==0 || strcmp("eND",codop)==0 || strcmp("ENd",codop)==0)
            tipo=2;
         if(strcmp("DW",codop)==0 || strcmp("dw",codop)==0 || strcmp("Dw",codop)==0 || strcmp("dW",codop)==0)
            tipo=3;
         if(strcmp("DB",codop)==0 || strcmp("db",codop)==0 || strcmp("Db",codop)==0 || strcmp("dB",codop)==0)
            tipo=4;
         if(strcmp("DC.W",codop)==0 || strcmp("dc.w",codop)==0 || strcmp("Dc.w",codop)==0 || strcmp("dC.w",codop)==0 || strcmp("dc.W",codop)==0 || strcmp("Dc.W",codop)==0 || strcmp("dC.W",codop)==0 || strcmp("DC.w",codop)==0)
            tipo=5;
         if(strcmp("DC.B",codop)==0 || strcmp("dc.b",codop)==0 || strcmp("Dc.b",codop)==0 || strcmp("dC.b",codop)==0 || strcmp("dc.B",codop)==0 || strcmp("Dc.B",codop)==0 || strcmp("dC.B",codop)==0 || strcmp("DC.b",codop)==0)
            tipo=6;
         if(strcmp("FCB",codop)==0 || strcmp("fcb",codop)==0 || strcmp("Fcb",codop)==0 || strcmp("fCb",codop)==0 || strcmp("fcB",codop)==0 || strcmp("FcB",codop)==0 || strcmp("fCB",codop)==0 || strcmp("FCb",codop)==0)
            tipo=7;
         if(strcmp("FDB",codop)==0 || strcmp("fdb",codop)==0 || strcmp("Fdb",codop)==0 || strcmp("fDb",codop)==0 || strcmp("fdB",codop)==0 || strcmp("FdB",codop)==0 || strcmp("fdB",codop)==0 || strcmp("FDb",codop)==0)
            tipo=8;
         if(strcmp("FCC",codop)==0 || strcmp("fcc",codop)==0 || strcmp("Fcc",codop)==0 || strcmp("fCc",codop)==0 || strcmp("fcC",codop)==0 || strcmp("FcC",codop)==0 || strcmp("fCC",codop)==0 || strcmp("FCc",codop)==0)
            tipo=9;
         if(strcmp("DS",codop)==0 || strcmp("ds",codop)==0 || strcmp("Ds",codop)==0 || strcmp("dS",codop)==0)
            tipo=10;
         if(strcmp("DS.B",codop)==0 || strcmp("ds.b",codop)==0 || strcmp("Ds.b",codop)==0 || strcmp("dS.b",codop)==0 || strcmp("ds.B",codop)==0 || strcmp("Ds.B",codop)==0 || strcmp("dS.B",codop)==0 || strcmp("DS.b",codop)==0)
            tipo=11;
         if(strcmp("DS.W",codop)==0 || strcmp("ds.w",codop)==0 || strcmp("Ds.w",codop)==0 || strcmp("dS.w",codop)==0 || strcmp("ds.W",codop)==0 || strcmp("Ds.W",codop)==0 || strcmp("dS.W",codop)==0 || strcmp("DS.W",codop)==0)
            tipo=12;
         if(strcmp("RMB",codop)==0 || strcmp("rmb",codop)==0 || strcmp("Rmb",codop)==0 || strcmp("rMb",codop)==0 || strcmp("rmB",codop)==0 || strcmp("RmB",codop)==0 || strcmp("rMB",codop)==0 || strcmp("RMb",codop)==0)
            tipo=13;
         if(strcmp("RMW",codop)==0 || strcmp("rmw",codop)==0 || strcmp("Rmw",codop)==0 || strcmp("rMw",codop)==0 || strcmp("rmW",codop)==0 || strcmp("RmW",codop)==0 || strcmp("rMW",codop)==0 || strcmp("RMW",codop)==0)
            tipo=14;
         if(strcmp("EQU",codop)==0 || strcmp("equ",codop)==0 || strcmp("Equ",codop)==0 || strcmp("eQu",codop)==0 || strcmp("eqU",codop)==0 || strcmp("EqU",codop)==0 || strcmp("eQU",codop)==0 || strcmp("EQu",codop)==0)
            tipo=15;
         if(tipo!=0)  //Si es alguna directiva no importando cual (If it´s a directive no matter which one)
         {
            correcto=1;  //Indica si el operando tiene un formato correcto, 1=si 0=no (Indicates if the operand has the correct format, 1=yes 0=no)

            //------------------------ORG, DW, DB, DC.W, DC.B, FCB, FDB, DS, DS.B, DS.W, RMB, RMW, EQU

            if(tipo==1 || tipo==3 || tipo==4  || tipo==5  || tipo==6  || tipo==7  || tipo==8  || tipo==10  || tipo==11  || tipo==12  || tipo==13  || tipo==14 || tipo==15)
            {
               signo=0;  //Indica la cantidad de signos encontrados (Indicates the amount of signes founded)
               if(tipo==1)  //ORG
               {
                  org++;
                  if(org>=2)
                     errores(74);
                  if(strcmp("null",etiqueta)!=0)
                     errores(75);
               }
               if(operando[0]=='$')  //Hexadecimal
               {
                  if(operando[1]=='\0')
                  {
                     errores(76);
                     correcto=0;
                  }
                  else
                  {
                     i=1;
                     while(operando[i]!='\0')
                     {
                        if(operando[i]!='0' && operando[i]!='1' && operando[i]!='2' && operando[i]!='3' && operando[i]!='4' && operando[i]!='5' && operando[i]!='6' && operando[i]!='7' && operando[i]!='8' && operando[i]!='9' && operando[i]!='a' && operando[i]!='b' && operando[i]!='c' && operando[i]!='d' && operando[i]!='e' && operando[i]!='f' && operando[i]!='A' && operando[i]!='B' && operando[i]!='C' && operando[i]!='D' && operando[i]!='E' && operando[i]!='F')
                        {
                           correcto=0;
                           errores(77);
                        }
                        i++;
                     }
                  }
               }
               else if(operando[0]=='@')  //Octal
               {
                  if(operando[1]=='\0')
                  {
                     errores(78);
                     correcto=0;
                  }
                  else
                  {
                     i=1;
                     while(operando[i]!='\0')
                     {
                        if(operando[i]!='0' && operando[i]!='1' && operando[i]!='2' && operando[i]!='3' && operando[i]!='4' && operando[i]!='5' && operando[i]!='6' && operando[i]!='7')
                        {
                           correcto=0;
                           errores(79);
                        }
                        i++;
                     }
                  }
               }
               else if(operando[0]=='%')  //Binario
               {
                  if(operando[1]=='\0')
                  {
                     errores(80);
                     correcto=0;
                  }
                  else
                  {
                     i=1;
                     while(operando[i]!='\0')
                     {
                        if(operando[i]!='0' && operando[i]!='1')
                        {
                           correcto=0;
                           errores(81);
                        }
                        i++;
                     }
                  }
               }
               else if(operando[0]=='0' || operando[0]=='1' || operando[0]=='2' || operando[0]=='3' || operando[0]=='4' || operando[0]=='5' || operando[0]=='6' || operando[0]=='7' || operando[0]=='8' || operando[0]=='9' || operando[0]=='-')  //Decimal
               {
                  if(operando[0]=='-')
                  {
                     signo=1;
                     i=1;
                  }
                  else
                     i=0;
                  while(operando[i]!='\0')
                  {
                     if(operando[i]=='-')
                     {
                        signo++;
                        correcto=0;
                     }
                     if(operando[i]!='0' && operando[i]!='1' && operando[i]!='2' && operando[i]!='3' && operando[i]!='4' && operando[i]!='5' && operando[i]!='6' && operando[i]!='7' && operando[i]!='8' && operando[i]!='9' && operando[i]!='-')
                     {
                        correcto=0;
                        errores(82);
                     }
                     i++;
                  }
                  if(signo>1)
                     errores(83);
                  if(operando[0]=='-' && operando[1]=='\0')
                     errores(84);
               }
               else
                  correcto=0;
               if(strcmp(operando,"null")==0)  //Si no hay operandos (If there isn´t operand)
               {
                  errores(85);
                  correcto=2;
                  if(tipo==15 && strcmp(etiqueta,"null")==0)
                     errores(86);
               }
               if(correcto==1)  //Si el operando esta correcto, no importando en cual base númerica, con que este bien en una (If the operand it´s correct, no matter in which numeric base, just need to be ok in one)
               {
                  if(tipo==4 || tipo==6 || tipo==7)  //DB, DC.B, FCB
                  {
                     if(extraer(operando,-1)>=0 && extraer(operando,-1)<=255)
                     {
                        a_temporal();
                        return 0;
                     }
                     else
                        errores(87);
                  }
                  else if(tipo==1 || tipo==3 || tipo==5  || tipo==8  || tipo==10  || tipo==11  || tipo==12  || tipo==13  || tipo==14 || tipo==15)  //DW, DC.W, FDB, DS, DS.B, RMB, DS.W, RMW
                  {
                     if(extraer(operando,-1)>=0 && extraer(operando,-1)<=65535)
                     {
                        if(tipo==1)  //ORG
                        {
                           if(org<=1 && strcmp(etiqueta,"null")==0)
                           {
                              a_temporal();
                              return 0;
                           }
                        }
                        else if(tipo==15)  //EQU
                        {
                           if(strcmp("null",etiqueta)==0)
                              errores(88);
                           else
                           {
                              a_temporal();
                              return 0;
                           }
                        }
                        a_temporal();
                        return 0;
                     }
                     else  //Si tiene un operando con rango invalido (If it has a operand with an invalid rank)
                     {
                        if(tipo==15)
                           if(strcmp("null",etiqueta)==0)  //EQU
                              errores(89);
                        errores(90);
                     }
                  }
               }
               else if(correcto==0)
               {
                  errores(91);
                  if(tipo==15)
                     if(strcmp("null",etiqueta)==0)
                        errores(92);
               }
            }  //Fin del si es ORG, DW, DB, DC.W, DC.B, FCB, FDB, DS, DS.B, DS.W, RMB, RMW o EQU

            //------------------------FCC

            else if(tipo==9)  //FCC
            {
               if(operando[0]=='"')
               {
                  correcto=0;
                  aux=1;
                  i=1;
                  while(operando[i]!='\0')
                  {
                     if(operando[i]=='"')
                        aux++;
                     i++;
                  }
                  if(aux>2)
                     errores(93);
                  if(aux==1)
                     errores(94);
                  if(aux==2)
                  {
                     a_temporal();
                     return 0;
                  }
               }
               if(strcmp(operando,"null")==0)  //Si no hay operandos (If there isn´t operands)
               {
                  errores(95);
                  correcto=2;
               }
               if(correcto==1)
                  errores(96);
            }

            //------------------------END

            else if(tipo==2)
            {
               lseek(fd,0L,2);
               no_end=1;
               a_temporal();
               if(strcmp(operando,"null")==0)
                  errores(97);
               else
                  errores(98);
               return 0;
            }
         }  //Fin del, si no es de tipo 0 (End of the "if it doesn´t tipe 0")
         else  //Si no es ninguna directiva todavía puede estar en el TABOP.txt (If it isn´t any directive it can be in the TABOP.txt)
            return 1;
      }  //Fin de la función directivas (End of the function directivas)

/***************************************************************a_temporal******************************************************************/

      void a_temporal()
      {
         int i,j,aux,aux2,posicion,posicion2,et_encontrada,desplazamiento;
         char etiqueta_t[20],CONTLOC_T[10],temporal,registro[7],rr[3],aa[3],pre_post[3],xb_8b[9],reg_16_b[16],num_hex2[20];
         et_encontrada=0;
         i=0;
         aux=0;
         for(j=0;j<80;j++)
            nombre_t[j]='\0';
         while(archivo3[i]!='\0' && aux!=1)
         {
            if(archivo3[i]!='0' && archivo3[i]!='1' && archivo3[i]!='2' && archivo3[i]!='3' && archivo3[i]!='4' && archivo3[i]!='5' && archivo3[i]!='6' && archivo3[i]!='7' && archivo3[i]!='8' && archivo3[i]!='9')
            {
               if(archivo3[i]!='.')
                  nombre_t[i]=archivo3[i];
               if(archivo3[i]=='.')
                  aux=1;
            }
            else  //Si es un numero (If it´s a number)
            {
               while(archivo3[i]=='0' || archivo3[i]=='1' || archivo3[i]=='2' || archivo3[i]=='3' || archivo3[i]=='4' || archivo3[i]=='5' || archivo3[i]=='6' || archivo3[i]=='7' || archivo3[i]=='8' || archivo3[i]=='9' && archivo3[i]!='\0')
               {
                  nombre_t[i]=archivo3[i];
                  aux=1;
                  i++;
               }
               i--;
            }
            i++;
         }
         nombre_t[i]='\0';
         strcpy(nombre_S19,nombre_t);
         strcat(nombre_S19,".S19");
         strcat(nombre_t,"tmp.txt");
         nombre_t[i+7]='\0';
         nombre_S19[strlen(nombre_S19)+4]='\0';
         posicion=lseek(fd,0L,1);
         close(fd);
         fd=open(nombre_t, 1);  //Primero vemos si ya se creo el TEMPORAL, si todavía no, pues ahy que crearlo (First we check if the TEMPORAL exists, if it doesn´t we have to create it)
         if(fd>0)  //Si ya existe (If it already exists)
         {
            aux=1;
            lseek(fd,0L,2);
            if(limpia_archivos==1)
            {
               close(fd);
               creat(nombre_t,666);
               fd=open(nombre_t, 1);
               lseek(fd,0L,0);
            }
         }
         else  //Aqui no existe (Here it doesn´t exists)
         {
            creat(nombre_t,666);
            fd=open(nombre_t, 1);
            if(fd>0)
            {
               aux=1;
               lseek(fd,0L,0);
            }
            else
               errores(99);
         }
         if(aux==1)
         {
            aux2=0;
            if(tipo_codop==1)  //Si es directiva (If it´s a directive)
            {
               if(tipo==4 || tipo==6 || tipo==7)  //Directivas de constantes, de 1 byte (DB, DC.B, FCB) (Constant directives, of 1 byte)
               {
                  write(fd,"CONTLOC",7);
                  write(fd,"\t",1);
                  write(fd,"\t",1);
                  dec_hex(CONTLOC,2);  //Se saca el CONTLOC en formato de cadena en hexadecimal completado a 2 bytes (We take the CONTLOC in hexadecimal format string completed to 2 bytes)
                  write(fd,num_hex,strlen(num_hex));  //Escribimos el valor del CONTLOC (We write the value of the CONTLOC)
                  write(fd,"\t",1);
                  write(fd,etiqueta,strlen(etiqueta));
                  write(fd,"\t",1);
                  write(fd,codop,strlen(codop));
                  write(fd,"\t",1);
                  write(fd,operando,strlen(operando));
                  write(fd,"\n",1);
                  a_tabsim();
                  CONTLOC=CONTLOC+1;
               }
               if(tipo==3 || tipo==5 || tipo==8)  //Directivas de constantes, de 2 bytes (DW, DC.W, FDB)
               {
                  write(fd,"CONTLOC",7);
                  write(fd,"\t",1);
                  write(fd,"\t",1);
                  dec_hex(CONTLOC,2);  //Se saca el CONTLOC en formato de cadena en hexadecimal completado a 2 bytes (We take the CONTLOC in hexadecimal format string completed to 2 bytes)
                  write(fd,num_hex,strlen(num_hex));  //Escribimos el valor del CONTLOC (We write the value of the CONTLOC)
                  write(fd,"\t",1);
                  write(fd,etiqueta,strlen(etiqueta));
                  write(fd,"\t",1);
                  write(fd,codop,strlen(codop));
                  write(fd,"\t",1);
                  write(fd,operando,strlen(operando));
                  write(fd,"\n",1);
                  a_tabsim();
                  CONTLOC=CONTLOC+2;
               }
               if(tipo==9)  //Directiva de constante, de caracteres (FCC) (Directive of constant of characters)
               {
                  write(fd,"CONTLOC",7);
                  write(fd,"\t",1);
                  write(fd,"\t",1);
                  dec_hex(CONTLOC,2);  //Se saca el CONTLOC en formato de cadena en hexadecimal completado a 2 bytes (We take the CONTLOC in hexadecimal format string completed to 2 bytes)
                  write(fd,num_hex,strlen(num_hex));  //Escribimos el valor del CONTLOC (We write the value of the CONTLOC)
                  write(fd,"\t",1);
                  write(fd,etiqueta,strlen(etiqueta));
                  write(fd,"\t",1);
                  write(fd,codop,strlen(codop));
                  write(fd,"\t",1);
                  write(fd,operando,strlen(operando));
                  write(fd,"\n",1);
                  a_tabsim();
                  aux2=strlen(operando)-2;
                  CONTLOC=CONTLOC+aux2;
               }
               if(tipo==10 || tipo==11 || tipo==13)  //Directivas de reserva de espacio en memoria, de un byte (DS, DS.B, RMB) (Directives of reserve from space of memory, 1 byte)
               {
                  write(fd,"CONTLOC",7);
                  write(fd,"\t",1);
                  write(fd,"\t",1);
                  dec_hex(CONTLOC,2);  //Se saca el CONTLOC en formato de cadena en hexadecimal completado a 2 bytes (We take the CONTLOC in hexadecimal format string completed to 2 bytes)
                  write(fd,num_hex,strlen(num_hex));  //Escribimos el valor del CONTLOC (We write the value of the CONTLOC)
                  write(fd,"\t",1);
                  write(fd,etiqueta,strlen(etiqueta));
                  write(fd,"\t",1);
                  write(fd,codop,strlen(codop));
                  write(fd,"\t",1);
                  write(fd,operando,strlen(operando));
                  write(fd,"\n",1);
                  a_tabsim();
                  CONTLOC=CONTLOC+extraer(operando,-1);  //(Valor del operando * 1) + el CONTLOC
                  linea_S19=1;
               }
               if(tipo==12 || tipo==14)  //Directivas de reserva de espacio en memoria, de 2 bytes (DS.W, RMW) (Directives of reserve of space in memory, 2 bytes)
               {
                  write(fd,"CONTLOC",7);
                  write(fd,"\t",1);
                  write(fd,"\t",1);
                  dec_hex(CONTLOC,2);  //Se saca el CONTLOC en formato de cadena en hexadecimal completado a 2 bytes (We take the CONTLOC in hexadecimal format string completed to 2 bytes)
                  write(fd,num_hex,strlen(num_hex));  //Escribimos el valor del CONTLOC (We write the value of the CONTLOC)
                  write(fd,"\t",1);
                  write(fd,etiqueta,strlen(etiqueta));
                  write(fd,"\t",1);
                  write(fd,codop,strlen(codop));
                  write(fd,"\t",1);
                  write(fd,operando,strlen(operando));
                  write(fd,"\n",1);
                  a_tabsim();
                  CONTLOC=CONTLOC+((extraer(operando,-1)) * 2);  //(Valor del operando * 2) + el CONTLOC
                  linea_S19=1;
               }
               if(tipo==15)  //EQU
               {
                  write(fd,"VALOR EQU",9);
                  write(fd,"\t",1);
                  aux2=extraer(operando,-1);  //Se saca el valor numerico del operando (We take the numerical value of the operand)
                  dec_hex(aux2,2);  //Se saca el valor numerico en formato de cadena en hexadecimal completado a 2 bytes (We take the CONTLOC in hexadecimal format string completed to 2 bytes)
                  write(fd,num_hex,strlen(num_hex));  //Escribimos el valor del CONTLOC (We write the value of the CONTLOC)
                  write(fd,"\t",1);
                  write(fd,etiqueta,strlen(etiqueta));
                  write(fd,"\t",1);
                  write(fd,codop,strlen(codop));
                  write(fd,"\t",1);
                  write(fd,operando,strlen(operando));
                  write(fd,"\n",1);
                  a_tabsim();
               }
               if(tipo==1)  //ORG
               {
                  write(fd,"DIR_INIC",8);
                  write(fd,"\t",1);
                  CONTLOC=0;
                  DIR_INIC=extraer(operando,-1);
                  CONTLOC=DIR_INIC;
                  dec_hex(CONTLOC,2);  //Se saca el CONTLOC en formato de cadena en hexadecimal completado a 2 bytes (We take the CONTLOC in hexadecimal format string completed to 2 bytes)
                  write(fd,num_hex,strlen(num_hex));  //Escribimos el valor del CONTLOC (We write the value of the CONTLOC)
                  write(fd,"\t",1);
                  write(fd,etiqueta,strlen(etiqueta));
                  write(fd,"\t",1);
                  write(fd,codop,strlen(codop));
                  write(fd,"\t",1);
                  write(fd,operando,strlen(operando));
                  write(fd,"\n",1);
                  a_tabsim();
                  strcpy(num_hex2,num_hex);  //Este lo ocupa el ORG (¡checa el codigo!) (This is required by the ORG)
                  strcpy(etiqueta2,etiqueta);  //Este lo ocupa el ORG (¡checa el codigo!) (This is required by the ORG)

                  //-------------------------------------Sacando el registro S0 del .S19
                  //-------------------------------------Obtaining the S0 from the .S19

                  j=(strlen(archivo2)/2)+3;  //Más 3 por que son 2 de la dirección y uno del checksum; entre 2 para considerarlo como pares (We add 3 because there are 2)
                  dec_hex(j,1);  //Sacando el campo de longitud (dirección + cant. de datos + checksum) (Obtaining the length field)
                  strcpy(cad_temp,num_hex);  //Ya quedo la longitud en cad_temp (Here we have the length in cad_temp)
                  cad_temp[2]='\0';
                  checksum(cad_temp,"0000",archivo2);  //Sacando el campo de checksum (Obtaining the checksum field)
                  S19("S0",cad_temp,"0000",archivo2,ch);
                  strcpy(num_hex,num_hex2);  //Este lo ocupa el ORG (¡checa el codigo!) (This is requiered by the ORG)
                  strcpy(etiqueta,etiqueta2);  //Este lo ocupa el ORG (¡checa el codigo!) (This is requiered by the ORG)
                  largo=0;  //Longitud del campo de datos del registro S1, desde ahorita lo inicializamos (Length of the data field from the register S1, since now we initialice it)

                  //--------------------------------------Terminamos de sacar el registro S0 del .S19
                  //--------------------------------------Finish for obtaining the register S0 from the .S19

               }
               if(tipo==2)  //END
               {
                  write(fd,"CONTLOC",7);
                  write(fd,"\t",1);
                  write(fd,"\t",1);
                  dec_hex(CONTLOC,2);  //Se saca el CONTLOC en formato de cadena en hexadecimal completado a 2 bytes (We take the CONTLOC in hexadecimal format string completed to 2 bytes)
                  write(fd,num_hex,strlen(num_hex));  //Escribimos el valor del CONTLOC (We write the value of the CONTLOC)
                  write(fd,"\t",1);
                  write(fd,etiqueta,strlen(etiqueta));
                  write(fd,"\t",1);
                  write(fd,codop,strlen(codop));
                  write(fd,"\t",1);
                  write(fd,operando,strlen(operando));
                  write(fd,"\n",1);
                  a_tabsim();
                  linea_S19=1;
               }

               //------------------------Dejamos de escribir e imprimimos en pantalla-------------------------
               //------------------------Stop writing and return printting on screen--------------------------

               if(paso_2==1)
               {
                  printf("%s\t%s\t%s\t%s\t",num_hex,etiqueta,codop,operando);  //Aquí ya imprimimos directamente del TEMPORAL (Here we print directly from the TEMPORAL)
                  if(largo==0)
                     strcpy(direccion,num_hex);
                  strcpy(direccion2,num_hex);
                  for(i=0;i<80;i++)
                     cod_maq2[i]='\0';  //Limpiamos cod_maq2 (Clean cod_maq2)
                  if(tipo==4 || tipo==6 || tipo==7)  //Constantes de un byte (DB = 4, DC.B = 6, FCB = 7) (Constants of 1 byte)
                  {
                     aux=extraer(operando,-1);
                     dec_hex(aux,1);
                     strcpy(cod_maq2,num_hex);
                     printf("%s",cod_maq2);
                     linea_S19=2;
                  }
                  if(tipo==3 || tipo==5 || tipo==8)  //Constantes de 2 bytes (DW = 3, DC.W = 5, FDB = 8) (Constants of 2 byte)
                  {
                     aux=extraer(operando,-1);
                     dec_hex(aux,2);
                     strcpy(cod_maq2,num_hex);
                     printf("%s",cod_maq2);
                     linea_S19=2;
                  }
                  if(tipo==9)  //Constante de caracteres (FCC) (Constant of characters)
                  {
                     i=0;
                     while(operando[i]!='\0')
                     {
                        if(operando[i]!='"')
                        {
                           j=operando[i];
                           dec_hex(j,1);
                           strcat(cod_maq2,num_hex);
                        }
                        i++;
                     }
                     printf("%s",cod_maq2);
                     linea_S19=2;
                  }
                  printf("\n\n");
               }

               //----------------------------Volvemos a escribir en el archivo---------------------------------
               //-----------------------------We back to write on the file----------------------------------

            }  //Fin del si es directiva (End of the "if it´s directive")
            else if(tipo_codop==2)  //Si es codop (Else if it´s codop)
            {
                 write(fd,"CONTLOC",7);
                 write(fd,"\t",1);
                 write(fd,"\t",1);
                 dec_hex(CONTLOC,2);  //Se saca el CONTLOC en formato de cadena en hexadecimal completado a 2 bytes (We take the CONTLOC in hexadecimal format string completed to 2 bytes)
                 write(fd,num_hex,strlen(num_hex));  //Escribimos el valor del CONTLOC (We write the value of the CONTLOC)
                 write(fd,"\t",1);
                 write(fd,etiqueta,strlen(etiqueta));
                 write(fd,"\t",1);
                 write(fd,codop,strlen(codop));
                 write(fd,"\t",1);
                 write(fd,operando,strlen(operando));
                 write(fd,"\n",1);
                 a_tabsim();

                 //-----------------------Dejamos de escribir e imprimimos en pantalla--------------------------
                 //------------------------Stop writing and return printting on screen--------------------------

                 if(paso_2==1)  //Solo mostramos en pantalla en el segundo paso de ensamblado (Only show in the screen in the second step of assembly)
                 {
                    printf("%s\t%s\t%s\t%s\t",num_hex,etiqueta,codop,operando);  //Aquí ya imprimimos directamente del TEMPORAL (Here we show directly on screen)
                    if(largo==0)
                       strcpy(direccion,num_hex);
                    strcpy(direccion2,num_hex);
                    for(i=0;i<80;i++)
                       cod_maq2[i]='\0';  //Limpiamos cod_maq2 (We clean cod_maq2)
                    if(modos==1)  //Si es inherente solo se toma su codigo maquina del TABOP (If it´s inherent only take his machine code from the TABOP)
                    {
                       strcpy(cod_maq2,cod_maq);
                       printf("%s",cod_maq2);
                       linea_S19=2;
                    }
                    else if(modos==2 || modos==3 || modos==4 || modos==5)  //Si es directo, extendido o inmediato (If it´s direct, extended or immediate)
                    {
                       strcpy(cod_maq2,cod_maq);
                       aux2=extraer(operando,-1);
                       if(modos==5)  //Si el modo extendido tiene una etiqueta en el operando (If the extended mode has a label in the operand)
                       {
                          if(operando[0]=='a' || operando[0]=='b' || operando[0]=='c' || operando[0]=='d' || operando[0]=='e' || operando[0]=='f' || operando[0]=='g' || operando[0]=='h' || operando[0]=='i' || operando[0]=='j' || operando[0]=='k' || operando[0]=='l' || operando[0]=='m' || operando[0]=='n' || operando[0]=='ñ' || operando[0]=='o' || operando[0]=='p' || operando[0]=='q' || operando[0]=='r' || operando[0]=='s' || operando[0]=='t' || operando[0]=='u' || operando[0]=='v' || operando[0]=='w' || operando[0]=='x' || operando[0]=='y' || operando[0]=='z' || operando[0]=='A' || operando[0]=='B' || operando[0]=='C' || operando[0]=='D' || operando[0]=='E' || operando[0]=='F' || operando[0]=='G' || operando[0]=='H' || operando[0]=='I' || operando[0]=='J' || operando[0]=='K' || operando[0]=='L' || operando[0]=='M' || operando[0]=='N' || operando[0]=='Ñ' || operando[0]=='O' || operando[0]=='P' || operando[0]=='Q' || operando[0]=='R' || operando[0]=='S' || operando[0]=='T' || operando[0]=='U' || operando[0]=='V' || operando[0]=='W' || operando[0]=='X' || operando[0]=='Y' || operando[0]=='Z')
                          {
                             posicion2=lseek(fd,0L,1);
                             close(fd);  //Se cierra el TEMPORAL.txt (We close the TEMPORAL.txt)
                             fd=open("C:\\TABSIM.txt", 2);  //Abrimos el TABSIM.txt (We open the TABSIM.txt)
                             lseek(fd,0L,0);  //Mandamos al principio de archivo (We send the cursor to the begining of the file)
                             while(read(fd,&temporal,1)>0 && et_encontrada==0)  //Buscamos la etiqueta en el TABSIM.txt (We search the label in the TABSIM.txt)
                             {
                                etiqueta_t[0]='\0';
                                CONTLOC_T[0]='\0';
                                while(read(fd,&temporal,1)>0 && temporal!='\t')  //Se recorre en la descripción (Here we step the descrption)
                                {
                                }
                                while(read(fd,&temporal,1)>0 && temporal=='\t')  //Se recorre en los tabuladores (Here we step on the tab)
                                {
                                }
                                i=1;
                                CONTLOC_T[0]='$';
                                CONTLOC_T[i]=temporal;
                                i++;
                                while(read(fd,&temporal,1)>0 && temporal!='\t')  //Llena el CONTLOC temporal (Fill the temporal CONTLOC)
                                {
                                   CONTLOC_T[i]=temporal;
                                   i++;
                                }
                                CONTLOC_T[i]='\0';
                                i=0;
                                while(read(fd,&temporal,1)>0 && temporal!='\n')  //Llena la etiqueta temporal  (Fill the temporal label)
                                {
                                   etiqueta_t[i]=temporal;
                                   i++;
                                }
                                etiqueta_t[i]='\0';
                                if(strcmp(operando,etiqueta_t)==0)  //Si la etiqueta se encuentra (If we find the label)
                                   et_encontrada=1;
                             }
                             if(et_encontrada==1)  //Si la etiqueta se encuentra (If we find the label)
                             {
                                aux2=extraer(CONTLOC_T,-1);
                                dec_hex(aux2,2);
                                et_encontrada=0;
                             }
                             else  //Si la etiqueta no se encuentra se ponen X porque no se sabe cuanto vale (If we don´t match with the label we put X´s because we don´t know the value)
                             {
                                strcpy(num_hex,"XXXX");
                                strcat(cod_maq2,num_hex);
                                printf("%s\n\n  --- Error: La etiqueta %s no se encontro en el TABSIM.txt",cod_maq2,operando);
                                et_encontrada=1;
                             }
                             close(fd);
                             fd=open(nombre_t,0);
                             lseek(fd,posicion2,0);
                          }  //Fin del si es una etiqueta (End of the "if it´s a label")
                       }  //Fin del si es extendido (modos==5) (End of the "if it´s extended")
                       if(modos==4)  //Si es directo que solo rellene 1 byte (If it´s direct we only fill with 1 byte)
                          dec_hex(aux2,1);
                       else  //Aquí va a rellenar segun convenga a 2 bytes (Here it´s going to fill to complete the 2 bytes)
                       {
                          dec_hex(aux2,2);
                          if(modos==2 || modos==3)  //Si es inmediato debemos revisar los bytes por calcular (If it´s immediate we need to check the bytes to calculate)
                          {
                             if(b_por_calc=='1')
                                dec_hex(aux2,1);
                             if(b_por_calc=='2')
                                dec_hex(aux2,2);
                          }
                       }
                       strcat(cod_maq2,num_hex);
                       cod_maq2[strlen(cod_maq2)]='\0';
                       if(et_encontrada==0)
                       {
                          printf("%s",cod_maq2);
                          linea_S19=2;
                       }
                       dec_hex(CONTLOC,2);
                    }  //Fin del si es directo, extendido o inmediato (End of the "if it´s direct")
                    else if(modos==6 || modos==7 || modos==8 || modos==10 || modos==11 || modos==12 || modos==13 || modos==14)  //Si es algun modo indizado simple (If it´s a simple indexed mode)
                    {
                       i=0;
                       j=0;
                       aux=0;
                       while(operando[i]!='\0')  //Sacamos el registro que tiene (despues de la coma) (We take the register that it has)
                       {
                          if(aux==1)
                          {
                             registro[j]=operando[i];
                             j++;
                          }
                          if(operando[i]==',')
                             aux=1;
                          i++;
                       }
                       registro[j]='\0';
                       if(registro[0]=='x' || registro[0]=='X')
                       {
                          strcpy(rr,"00");
                          if(registro[1]=='-')
                             pre_post[0]='1';
                          if(registro[1]=='+')
                             pre_post[0]='1';
                       }
                       if(registro[0]=='y' || registro[0]=='Y')
                       {
                          strcpy(rr,"01");
                          if(registro[1]=='-')
                             pre_post[0]='1';
                          if(registro[1]=='+')
                             pre_post[0]='1';
                       }
                       if(registro[0]=='s' || registro[0]=='S')
                          if(registro[1]=='p' || registro[1]=='P')
                          {
                             strcpy(rr,"10");
                             if(registro[2]=='-')
                                pre_post[0]='1';
                             if(registro[2]=='+')
                                pre_post[0]='1';
                          }
                       if(registro[0]=='p' || registro[0]=='P')
                          if(registro[1]=='c' || registro[1]=='C')
                          {
                             strcpy(rr,"11");
                             if(registro[2]=='-')
                                pre_post[0]='1';
                             if(registro[2]=='+')
                                pre_post[0]='1';
                          }
                       if(registro[0]=='-')
                       {
                          pre_post[0]='0';
                          if(registro[1]=='x' || registro[1]=='X')
                             strcpy(rr,"00");
                          if(registro[1]=='y' || registro[1]=='Y')
                             strcpy(rr,"01");
                          if(registro[1]=='s' || registro[1]=='S')
                             strcpy(rr,"10");
                          if(registro[1]=='p' || registro[1]=='P')
                             strcpy(rr,"11");
                       }
                       if(registro[0]=='+')
                       {
                          pre_post[0]='0';
                          if(registro[1]=='x' || registro[1]=='X')
                             strcpy(rr,"00");
                          if(registro[1]=='y' || registro[1]=='Y')
                             strcpy(rr,"01");
                          if(registro[1]=='s' || registro[1]=='S')
                             strcpy(rr,"10");
                          if(registro[1]=='p' || registro[1]=='P')
                             strcpy(rr,"11");
                       }
                       rr[3]='\0';
                       pre_post[1]='\0';
                       strcpy(cod_maq2,cod_maq);
                       if(operando[0]==',')
                          aux2=0;  //Si el operando indizado empieza con coma se toma ese valor como cero (If the indexed operand starts with a comma we take that value has a zero)
                       else
                          aux2=extraer(operando,-1);  //Sacamos lo que vale la parte izquierda del operando indizado (esto,xysp) (We take what it´s worth from the left part of the indexed operand)
                       if(modos==6)  //Indizado de 5 bits (su formula es rr0nnnnn) (Indexed of 5 bits)
                       {
                          dec_bin(aux2,5);
                          xb_8b[0]='\0';
                          strcpy(xb_8b,rr);
                          strcat(xb_8b,"0");
                          strcat(xb_8b,binario);  //nnnnn
                          xb_8b[8]='\0';
                          divide(xb_8b);  //Se parte en 2 y se saca su representación hexadecimal (We split it in 2 parts and get his hexadecimal representation)
                          strcat(cod_maq2,xb);  //xb
                          cod_maq2[5]='\0';
                          printf("%s",cod_maq2);
                          linea_S19=2;
                       }
                       if(modos==7)  //Indizado de 9 bits (xb ff; su formula es 111rr0zs, con z=0, s=1 si es negativo) (Indexed of 9 bits)
                       {
                          xb_8b[0]='\0';
                          strcpy(xb_8b,"111");
                          strcat(xb_8b,rr);
                          strcat(xb_8b,"00");
                          if(extraer(operando,-1)<0)  //S
                             strcat(xb_8b,"1");
                          else
                             strcat(xb_8b,"0");
                          xb_8b[8]='\0';
                          divide(xb_8b);  //Se parte en 2 y se saca su representación hexadecimal (We split it in 2 parts and get his hexadecimal representation)
                          strcat(cod_maq2,xb);  //Se le ponen los 2 bytes del xb (We put the 2 bytes from the xb)
                          dec_hex(aux2,1);
                          if(aux2<0)  //Si (esto,xysp) es negativo (If it´s negative)
                          {
                             dec_bin(aux2,8);  //Para que le saque su complemento a 2 (To get his complement to 2)
                             divide(binario);
                             strcpy(num_hex,xb);
                          }
                          strcat(cod_maq2,num_hex);  //ff
                          cod_maq2[6]='\0';
                          printf("%s",cod_maq2);
                          linea_S19=2;
                       }
                       if(modos==8)  //Indizado de 16 bits (xb ee ff; su formula es 111rr0zs, con z=1, s=bit de signo) (Indexed of 16 bits)
                       {
                          xb_8b[0]='\0';
                          strcpy(xb_8b,"111");
                          strcat(xb_8b,rr);
                          strcat(xb_8b,"010");
                          xb_8b[8]='\0';
                          divide(xb_8b);  //Se parte en 2 y se saca su representación hexadecimal (We split it in 2 parts and get his hexadecimal representation)
                          strcat(cod_maq2,xb);  //Se le ponen los 2 bytes del xb (We put the 2 bytes from the xb)
                          dec_hex(aux2,2);
                          strcat(cod_maq2,num_hex);  //ee ff
                          cod_maq2[8]='\0';
                          printf("%s",cod_maq2);
                          linea_S19=2;
                       }
                       if(modos==10 || modos==11 || modos==12 || modos==13)  //Indizado de pre/post incremento/incremento (Indexed of pre/post increment/decrement)
                       {  //(Su formula es rr1pnnnn, donde p=post=1, p=pre=1, nnnn = número en 2 bytes)
                          xb_8b[0]='\0';
                          strcpy(xb_8b,rr);
                          strcat(xb_8b,"1");
                          if(modos==10 || modos==12)  //P = pre
                             strcat(xb_8b,"0");
                          else  //P = post
                             strcat(xb_8b,"1");
                          if(modos==10 || modos==11)  //Si es decremento se toma como negativo (If it´s a decrement we take him as a negative)
                             dec_bin((aux2)*-1,4);
                          else
                             dec_bin(aux2-1,4);
                          strcat(xb_8b,binario);  //nnnn
                          xb_8b[8]='\0';
                          divide(xb_8b);  //Se parte en 2 y se saca su representación hexadecimal (We split it in 2 parts and get his hexadecimal representation)
                          strcat(cod_maq2,xb);  //Se le ponen los 2 bytes del xb (We put the 2 bytes from the xb)
                          cod_maq2[5]='\0';
                          printf("%s",cod_maq2);
                          linea_S19=2;
                       }
                       if(modos==14)  //Indizado de acumulador (su formula es 111rr1aa, con aa=a=00, aa=b=01, aa=d=10) (Indexed of acumulator)
                       {
                          xb_8b[0]='\0';
                          strcpy(xb_8b,"111");
                          strcat(xb_8b,rr);
                          strcat(xb_8b,"1");
                          if(operando[0]=='a' || operando[0]=='A')
                             strcpy(aa,"00");
                          if(operando[0]=='b' || operando[0]=='B')
                             strcpy(aa,"01");
                          if(operando[0]=='d' || operando[0]=='D')
                             strcpy(aa,"10");
                          aa[3]='\0';
                          strcat(xb_8b,aa);
                          xb_8b[8]='\0';
                          divide(xb_8b);  //Se parte en 2 y se saca su representación hexadecimal (We split it in 2 parts and get his hexadecimal representation)
                          strcat(cod_maq2,xb);  //Se le ponen los 2 bytes del xb (We put the 2 bytes from the xb)
                          cod_maq2[5]='\0';
                          printf("%s",cod_maq2);
                          linea_S19=2;
                       }
                    }  //Fin del si es algun tipo de indizado simple (End of the "if it´s some simple indexed")
                    else if(modos==9 || modos==15)  //Si es algun modo indizado indirecto (If it´s some simple indexed)
                    {
                       i=1;  //Recorremos hasta la coma para sacar rr (Here we step the comma to get the rr)
                       aux=0;  //Para indicar cuando se encuentre la coma (To indicate when we find the comma)
                       while(operando[i]!='\0' && aux==0)
                       {
                          if(operando[i]==',')
                             aux=1;
                          i++;
                       }
                       if(operando[i]=='x' || operando[i]=='X')
                          strcpy(rr,"00");
                       if(operando[i]=='y' || operando[i]=='Y')
                          strcpy(rr,"01");
                       if(operando[i]=='s' || operando[i]=='S')
                          strcpy(rr,"10");
                       if(operando[i]=='p' || operando[i]=='P')
                          strcpy(rr,"11");
                       rr[3]='\0';
                       strcpy(cod_maq2,cod_maq);
                       if(modos==9)  //Indizado indirecto de 16 bits (xb ee ff; su formula es 111rr011) (Indexed indirect of 16 bits)
                       {
                          xb_8b[0]='\0';
                          strcpy(xb_8b,"111");
                          strcat(xb_8b,rr);
                          strcat(xb_8b,"011");
                          xb_8b[8]='\0';
                          divide(xb_8b);  //Se parte en 2 y se saca su representación hexadecimal (We split it in 2 parts and get his hexadecimal representation)
                          strcat(cod_maq2,xb);  //Se le ponen los 2 bytes del xb (We put the 2 bytes from the xb)
                          aux=extraer(operando,-1);
                          dec_hex(aux,2);
                          strcat(cod_maq2,num_hex);  //ee ff
                          cod_maq2[8]='\0';
                          printf("%s",cod_maq2);
                          linea_S19=2;
                       }
                       if(modos==15)  //Indizado de acumulador indirecto (su formula es 111rr111) (Indexed of indirect acumulator)
                       {
                          xb_8b[0]='\0';
                          strcpy(xb_8b,"111");
                          strcat(xb_8b,rr);
                          strcat(xb_8b,"111");
                          xb_8b[8]='\0';
                          divide(xb_8b);  //Se parte en 2 y se saca su representación hexadecimal (We split it in 2 parts and get his hexadecimal representation)
                          strcat(cod_maq2,xb);  //Se le ponen los 2 bytes del xb (We put the 2 bytes from the xb)
                          cod_maq2[5]='\0';
                          printf("%s",cod_maq2);
                          linea_S19=2;
                       }
                    }  //Fin del si es algun tipo de indizado indirecto (End of the "if it´s any indirect indexed")
                    else if(modos==16 || modos==17)  //Relativo de 8 ó 16 bits (Relative of 8 or 16 bits)
                    {
                       posicion2=lseek(fd,0L,1);
                       close(fd);  //Se cierra el TEMPORAL.txt (We close the TABOP.txt)
                       fd=open("C:\\TABSIM.txt", 2);  //Abrimos el TABSIM.txt (We open the TABSIM.txt)
                       lseek(fd,0L,0);  //Mandamos al principio de archivo (Send to the begining of the file)
                       while(read(fd,&temporal,1)>0 && et_encontrada==0)  //Buscamos la etiqueta en el TABSIM.txt (We search for the label in the TABSIM.txt)
                       {
                          etiqueta_t[0]='\0';
                          CONTLOC_T[0]='\0';
                          while(read(fd,&temporal,1)>0 && temporal!='\t')  //Se recorre en la descripción (Here we step the descrption)
                          {
                          }
                          while(read(fd,&temporal,1)>0 && temporal=='\t')  //Se recorre en los tabuladores (Here we step the tab´s)
                          {
                          }
                          i=1;
                          CONTLOC_T[0]='$';
                          CONTLOC_T[i]=temporal;
                          i++;
                          while(read(fd,&temporal,1)>0 && temporal!='\t')  //Llena el CONTLOC temporal (We fill the temporal CONTLOC)
                          {
                             CONTLOC_T[i]=temporal;
                             i++;
                          }
                          CONTLOC_T[i]='\0';
                          i=0;
                          while(read(fd,&temporal,1)>0 && temporal!='\n')  //Llena la etiqueta temporal (We fill the temporal label)
                          {
                             etiqueta_t[i]=temporal;
                             i++;
                          }
                          etiqueta_t[i]='\0';
                          if(strcmp(operando,etiqueta_t)==0)  //Si la etiqueta se encuentra (If we find the label)
                             et_encontrada=1;
                       }
                       if(et_encontrada==1)  //Si la etiqueta se encuentra (If we find the label)
                       {
                          strcpy(cod_maq2,cod_maq);
                          if(total_b=='1')
                             CONTLOC=CONTLOC+1;  //Total de bytes + CONTLOC (Total bytes + CONTLOC)
                          if(total_b=='2')
                             CONTLOC=CONTLOC+2;  //Total de bytes + CONTLOC (Total bytes + CONTLOC)
                          if(total_b=='3')
                             CONTLOC=CONTLOC+3;  //Total de bytes + CONTLOC (Total bytes + CONTLOC)
                          if(total_b=='4')
                             CONTLOC=CONTLOC+4;  //Total de bytes + CONTLOC (Total bytes + CONTLOC)
                          desplazamiento=extraer(CONTLOC_T,-1)-CONTLOC;  //El desplazamiento en decimal (The displacement in decimal)
                          if(desplazamiento>=0)  //Positivo (Positive)
                          {
                             if(modos==17)  //Relativo de 8 bits (Relative of 8 bits)
                             {
                                if(desplazamiento<=127)
                                {
                                   dec_hex(desplazamiento,1);
                                   strcat(cod_maq2,num_hex);
                                   printf("%s",cod_maq2);
                                   linea_S19=2;
                                }
                                else
                                   errores(104);
                             }
                             if(modos==16)  //Relativo de 16 bits (Relative of 16 bits)
                             {
                                if(desplazamiento<=32767)
                                {
                                   dec_hex(desplazamiento,2);
                                   strcat(cod_maq2,num_hex);
                                   printf("%s",cod_maq2);
                                   linea_S19=2;
                                }
                                else
                                   errores(104);
                             }
                          }
                          else if(desplazamiento<0)  //Negativo (Negative)
                          {
                             if(modos==17)  //Relativo negativo de 8 bits (Relative negative of 8 bits)
                             {
                                if(desplazamiento>=-128)
                                {
                                   dec_bin(desplazamiento,8);  //Se saca el complemento del desplazamiento (We get the complement of the displacement)
                                   divide(binario);  //Se saca su representación hexadecimal en 2 bytes (We get his hexadecimal representation in 2 bytes)
                                   strcat(cod_maq2,xb);
                                   printf("%s",cod_maq2);
                                   linea_S19=2;
                                }
                                else
                                   errores(104);
                             }
                             if(modos==16)  //Relativo negativo de 16 bits (Negative relative of 16 bits)
                             {
                                if(desplazamiento>=-32768)
                                {
                                   dec_bin(desplazamiento,16);  //Binario tiene la representación con complemento a 2 en 16 bits (Binario has the complement to 2 representation in 16 bits)
                                   for(i=0;i<8;i++)  //Le pasamos la primer mitad (izq) a binario_t (We pass the first half to binario_t)
                                      binario_t[i]=binario[i];
                                   binario_t[8]='\0';
                                   divide(binario_t);  //A esta mitad le sacamos su representación hexadecimal a 2 bytes (To this half we get his hexadecimal representation)
                                   strcat(cod_maq2,xb);  //Y se lo pasamos a cod_maq2 (And we pass to cod_maq2)
                                   binario_t[0]='\0';
                                   j=0;
                                   for(i=8;i<16;i++)  //Le pasamos la segunda mitad (der) a binario_t (We pass the second half to binario_t)
                                   {
                                      binario_t[j]=binario[i];
                                      j++;
                                   }
                                   binario_t[8]='\0';
                                   divide(binario_t);  //A esta mitad le sacamos su representación hexadecimal a 2 bytes (To this half we get his hexadecimal representation)
                                   strcat(cod_maq2,xb);  //Y se lo pasamos a cod_maq2 (And we pass to cod_maq2)
                                   printf("%s",cod_maq2);
                                   linea_S19=2;
                                }
                                else
                                   errores(104);
                             }
                          }
                          et_encontrada=0;
                       }
                       else  //Si la etiqueta no se encuentra se ponen X porque no se sabe cuanto vale (If we don´t match with the label we put X´s because we don´t know the value)
                       {
                          strcpy(num_hex,"XXXX");
                          strcat(cod_maq2,num_hex);
                          printf("%s\n\n  --- Error: La etiqueta %s no se encontro en el TABSIM.txt",cod_maq,operando);
                          et_encontrada=1;
                       }
                       close(fd);
                       fd=open(nombre_t,0);
                       lseek(fd,posicion2,0);
                       if(total_b=='1')
                          CONTLOC=CONTLOC-1;  //Total de bytes + CONTLOC (Total bytes + CONTLOC)
                       if(total_b=='2')
                          CONTLOC=CONTLOC-2;  //Total de bytes + CONTLOC (Total bytes + CONTLOC)
                       if(total_b=='3')
                          CONTLOC=CONTLOC-3;  //Total de bytes + CONTLOC (Total bytes + CONTLOC)
                       if(total_b=='4')
                          CONTLOC=CONTLOC-4;  //Total de bytes + CONTLOC (Total bytes + CONTLOC)
                    }  //Fin del si es relativo de 8 ó 16 bits (End of the "if it´s 8 or 16 bits relative")
                    printf("\n\n");
                 }  //Fin del si es el segundo paso del ensamblador

                 //-----------------------------Volvemos a escribir en el archivo--------------------------------
                 //--------------------------------We back to write on the file----------------------------------

                 if(total_b=='1')
                    CONTLOC=CONTLOC+1;  //Total de bytes + CONTLOC (Total bytes + CONTLOC)
                 if(total_b=='2')
                    CONTLOC=CONTLOC+2;  //Total de bytes + CONTLOC (Total bytes + CONTLOC)
                 if(total_b=='3')
                    CONTLOC=CONTLOC+3;  //Total de bytes + CONTLOC (Total bytes + CONTLOC)
                 if(total_b=='4')
                    CONTLOC=CONTLOC+4;  //Total de bytes + CONTLOC (Total bytes + CONTLOC)
            }  //Fin del si es codop (End of the "if ti´s codop")
            if(org!=0)
               if(CONTLOC>65535)
                  errores(100);
            if(org==0)
               if(tipo!=15)
                  errores(101);
         }  //Fin del si se abrio correctamente el TEMPORAL (End of the "if the TEMPORAL opened correctly ")
         close(fd);
         fd=open(archivo,0);
         lseek(fd,posicion,0);
      }  //Fin de la función a_temporal (End of the a_temporal function)

/****************************************************************a_tabsim*******************************************************************/

      void a_tabsim()
      {
         int i,aux,aux2,posicion;
         char temporal,etiqueta_t[20];
         if(paso_2==1)
            return;
         if(strcmp(etiqueta,"null")==0)
            return;
         posicion=lseek(fd,0L,1);
         close(fd);
         fd=open("C:\\TABSIM.txt", 2);  //Primero vemos si ya se creo el TABSIM.txt, si todavía no, pues ahy que crearlo (First we check if the TABSIM.txt exists, if it doesn´t we have to create it)
         if(fd>0)  //Si ya existe
         {
            aux=1;
            if(limpia_archivos==1 || limpia_archivos==0 && limpia_tabsim==0)  //Solo si es la primera vez que verifica una (This is only the first time it check one)
            {  //etiqueta va a entrar para sobreescribir el TABSIM.txt (label to enter and overwrite the TABSIM.txt)
               close(fd);
               creat("C:\\TABSIM.txt",666);
               fd=open("C:\\TABSIM.txt", 2);
               lseek(fd,0L,0);
               limpia_tabsim=1;
            }
         }
         else  //Aqui no existe (Here it doen´t exists)
         {
            creat("C:\\TABSIM.txt",666);
            fd=open("C:\\TABSIM.txt", 2);
            lseek(fd,0L,0);
            if(fd>0)
               aux=1;
            else
               errores(102);
         }
         if(aux==1)
         {
            etiqueta_t[0]='\0';
            lseek(fd,0L,0);
            while(read(fd,&temporal,1)>0)  //Verificamos si la etiqueta ya esta en el TABSIM.txt (We check if the label is already in the TABSIM.txt)
            {
               while(read(fd,&temporal,1)>0 && temporal!='\t')  //Se recorre en la descripción (We step the descroption)
               {
               }
               while(read(fd,&temporal,1)>0 && temporal=='\t')  //Se recorre en los tabuladores (We step the tab´s)
               {
               }
               while(read(fd,&temporal,1)>0 && temporal!='\t')  //Se recorre en el CONTLOC (We step the CONTLOC)
               {
               }
               i=0;
               while(read(fd,&temporal,1)>0 && temporal!='\n')  //Llena la etiqueta temporal (Fill the temporal label)
               {
                  etiqueta_t[i]=temporal;
                  i++;
               }
               etiqueta_t[i]='\0';
               if(strcmp(etiqueta,etiqueta_t)==0)
               {
                  errores(103);
                  return;
               }
            }
            lseek(fd,0L,2);
            aux2=0;
            if(tipo_codop==1)  //Si es directiva (If it´s directive)
            {
               if(tipo!=15)  //Si no es EQU
                  write(fd,"CONTLOC (ETIQUETA RELATIVA)",27);
               else
               {
                  write(fd,"EQU (ETIQUETA ABSOLUTA)",23);
                  write(fd,"\t",1);
               }
               write(fd,"\t",1);
               write(fd,num_hex,strlen(num_hex));  //Escribimos el valor numerico en hexadecimal del CONTLOC (We write the hexadecimal numerical value from the CONTLOC)
               write(fd,"\t",1);
               write(fd,etiqueta,strlen(etiqueta));
               write(fd,"\n",1);
               limpia_tabsim=1;
            }  //Fin del si es directiva (End of the "if it´s directive")
            else if(tipo_codop==2)  //Si es codop
            {
               write(fd,"CONTLOC (ETIQUETA RELATIVA)",27);
               write(fd,"\t",1);
               write(fd,num_hex,strlen(num_hex));  //Escribimos el valor numerico en hexadecimal del CONTLOC (We write the hexadecimal numerical value from the CONTLOC)
               write(fd,"\t",1);
               write(fd,etiqueta,strlen(etiqueta));
               write(fd,"\n",1);
               limpia_tabsim=1;
            }
         }  //Fin del si se abrio correctamente el TABSIM (End of the "if we open correctly the TABSIM")
         close(fd);
         fd=open(archivo,0);
         lseek(fd,posicion,0);
      }  //Fin de la función a_tabsim (End of the a_tabsim function)

/*****************************************************************dec_bin*******************************************************************/

      void dec_bin(int numero, int completar)  //cadena a transformar, bytes por completar (string to transform, bytes to calculate)
      {
         int i,j,negativo;
         negativo=0;
         if(numero<0)
            negativo=1;
         i=0;
         j=0;
         while(j==0)  //Va a sacar el numero por el metodo de divisiones sucesivas pero lo va a dejar al revez (We are gonna get the number by the succesive division´s but it´s goning to let it forward)
         {
            if((numero%2)==0)
               binario_t[i]='0';
            else
               binario_t[i]='1';
            numero=numero/2;
            if(numero==0)
               j=1;
            i++;
         }
         binario_t[i]='\0';
         j=strlen(binario_t);
         if(completar==4)  //Completamos para darle un formato de 4 bits (We complete to give him a 4 bits format)
         {
            if(j==1)
               for(i=1;i<4;i++)
                  binario_t[i]='0';
            else if(j==2)
               for(i=2;i<4;i++)
                  binario_t[i]='0';
            else if(j==3)
               for(i=3;i<4;i++)
                  binario_t[i]='0';
            binario_t[i]='\0';
         }
         else if(completar==5)  //Completamos para darle un formato de 5 bits (We complete to give him a 5 bits format)
         {
            if(j==1)
               for(i=1;i<5;i++)
                  binario_t[i]='0';
            else if(j==2)
               for(i=2;i<5;i++)
                  binario_t[i]='0';
            else if(j==3)
               for(i=3;i<5;i++)
                  binario_t[i]='0';
            else if(j==4)
               for(i=4;i<5;i++)
                  binario_t[i]='0';
            binario_t[i]='\0';
         }
         else if(completar==8)  //Completamos para darle un formato de 8 bits  (We complete to give him a 8 bits format)
         {
            if(j==1)
               for(i=1;i<8;i++)
                  binario_t[i]='0';
            else if(j==2)
               for(i=2;i<8;i++)
                  binario_t[i]='0';
            else if(j==3)
               for(i=3;i<8;i++)
                  binario_t[i]='0';
            else if(j==4)
               for(i=4;i<8;i++)
                  binario_t[i]='0';
            else if(j==5)
               for(i=5;i<8;i++)
                  binario_t[i]='0';
            else if(j==6)
               for(i=6;i<8;i++)
                  binario_t[i]='0';
            else if(j==7)
               for(i=7;i<8;i++)
                  binario_t[i]='0';
            binario_t[i]='\0';
         }
         else if(completar==16)
         {
            if(j!=16)
            {
               for(j=j;j<16;j++)
                  binario_t[j]='0';
               binario_t[j]='\0';
               i=j;
            }
         }
         j=0;
         i--;
         while(i>=0)  //Se lo pasamos ahora si como debe de ser (Now we pass him has it has to be)
         {
            binario[j]=binario_t[i];
            i--;
            j++;
         }
         binario[j]='\0';  //Si es positivo aquí ya quedo (If it´s positive here it´s done)
         j=strlen(binario);
         if(j>8 && completar!=16)  //Si es un número más grande que 8 bits (If it´s a bigger than 8 bits number)
         {
            i=7;
            j--;  //Para no contar el fin de cadena (For not count the end of string)
            while(i>=0)  //Le pasamos a binatio_t lo que tiene binario pero desde su final, los primeros 8 bits hasta donde (We pass to binario_t everithing that binario has since his end, the first 8 bits until him is full)
            {  //alcance a llenarse y despues esto (ya en 8 bits) se lo volvemos a pasar a binario (and after that we pass him to binario again)
               binario_t[i]=binario[j];
               i--;
               j--;
            }
            binario_t[8]='\0';
            strcpy(binario,binario_t);
         }
         if(j>16 && completar==16)
         {
            i=15;
            j--;  //Para no contar el fin de cadena (For not count the end of string)
            while(i>=0)  //Le pasamos a binatio_t lo que tiene binario pero desde su final, los primeros 8 bits hasta donde (We pass to binario_t everithing that binario has since his end, the first 8 bits until him is full)
            {  //alcance a llenarse y despues esto (ya en 8 bits) se lo volvemos a pasar a binario (ya en 8 bits) se lo volvemos a pasar a binario (and after that we pass him to binario again)
               binario_t[i]=binario[j];
               i--;
               j--;
            }
            binario_t[16]='\0';
            strcpy(binario,binario_t);
         }
         if(negativo==1)
         {
            binario_t[0]='%';  //Para que extraer() lo tome como binario (To extraer () trate him as binary)
            j=1;
            i=0;
            while(binario[i]!='\0')  //Invertimos los bits (Invert bits)
            {
               if(binario[i]=='1')
                  binario_t[j]='0';
               else if(binario[i]=='0')
                  binario_t[j]='1';
               j++;
               i++;
            }
            binario_t[j]='\0';  //Aquí ya quedo en complemento a 1 almacenada en un temporal (Here it´s done in complement to 1 an store in temporal)
            j=extraer(binario_t,-1)+1;  //complemento a 2 (Complement to 2)
            dec_bin(j,completar);
         }
      }  //Fin de la función dec_bin (End of the dec_bin function)

/*****************************************************************dec_hex*******************************************************************/

      void dec_hex(int numero, int completar)  //cadena a transformar, bytes por completar (String to transform, bytes to calculate)
      {
         int i,j;
         char caracter,aux_num_hex[20];
         if(numero<0)
         {
            strcpy(num_hex,"XXXX");
            num_hex[4]='\0';
            return;
         }
         for(i=0;i<20;i++)
            aux_num_hex[i]='\0';
         for(i=0;i<20;i++)
            num_hex[i]='\0';
         i=0;
         j=0;
         while(j==0)  //Va a sacar el numero por el metodo de divisiones sucesivas pero lo va a dejar al revez (We are gonna get the number by the succesive division´s but it´s goning to let it forward)
         {
            if((numero%16)>=0 && (numero%16)<=9)
            {
               caracter=(numero%16)+48;
               aux_num_hex[i]=caracter;
            }
            else if((numero%16)>=10 && (numero%16)<=15)
            {
               caracter=(numero%16)+55;
               aux_num_hex[i]=caracter;
            }
            numero=numero/16;
            if(numero==0)
               j=1;
            i++;
         }
         aux_num_hex[i]='\0';
         j=strlen(aux_num_hex);
         if(completar==0)  //Sin completar bytes (Without complete bytes)
         {
            strcpy(num_hex,aux_num_hex);
            return;
         }
         if(completar==1)  //Aquí ya es completando especificamente a una magnitud (2 bytes en este caso, modo directo)
         {
            if(strlen(aux_num_hex)==2)  //Si el numero hexadecimal es de 2 bytes (por ejemplo FF) que no lo rellene
            {
               j=0;
               i=strlen(aux_num_hex);
               i--;  //Para no contar el fin de cadena (For not count the end of string)
               while(i>=0)  //Se lo pasamos ahora si como debe de ser (We pass to it like it has to be)
               {
                  num_hex[j]=aux_num_hex[i];
                  i--;
                  j++;
               }
               num_hex[j]='\0';
               return;
            }
            strcpy(num_hex,"0");
            strcat(num_hex,aux_num_hex);
            return;  //Para que se salga sin hacer lo demás (To get off without do anything)
         }
         if(strlen(aux_num_hex)==1)  //Pone los 0 que hacen falta para completarlo a 4 bytes, si el numero generado mide 1 (Put zero´s until we complete him to 4 bytes, if the number lenght it´s 1)
         {
            for(i=1;i<4;i++)
               aux_num_hex[i]='0';
            aux_num_hex[i]='\0';
            strcat(num_hex,aux_num_hex);
         }
         if(strlen(aux_num_hex)==2)  //Pone los 0 que hacen falta para completarlo a 4 bytes, si el numero generado mide 2 (Put zero´s until we complete him to 4 bytes, if the number lenght it´s 2)
         {
            for(i=2;i<4;i++)
               aux_num_hex[i]='0';
            aux_num_hex[i]='\0';
            strcat(num_hex,aux_num_hex);
         }
         if(strlen(aux_num_hex)==3)  //Pone los 0 que hacen falta para completarlo a 4 bytes, si el numero generado mide 3 (Put zero´s until we complete him to 4 bytes, if the number lenght it´s 4)
         {
            for(i=3;i<4;i++)
               aux_num_hex[i]='0';
            aux_num_hex[i]='\0';
            strcat(num_hex,aux_num_hex);
         }
         if(strlen(aux_num_hex)==4)  //Pone los 0 que hacen falta para completarlo a 4 bytes, si el numero generado mide 4 (Put zero´s until we complete him to 4 bytes, if the number lenght it´s 4)
            strcpy(num_hex,aux_num_hex);
         if(strlen(aux_num_hex)==5)  //Pone los 0 que hacen falta para completarlo a 6 bytes, si el numero generado mide 5 (Put zero´s until we complete him to 6 bytes, if the number lenght it´s 5)
         {
            for(i=5;i<6;i++)
               aux_num_hex[i]='0';
            aux_num_hex[i]='\0';
            strcat(num_hex,aux_num_hex);
         }
         j=0;
         i=strlen(aux_num_hex);
         i--;  //Para no contar el fin de cadena (For not count the end of string)
         while(i>=0)  //Se lo pasamos ahora si como debe de ser (We pass to him like it should be)
         {
            num_hex[j]=aux_num_hex[i];
            i--;
            j++;
         }
         num_hex[j]='\0';
      }  //Fin de la función dec_hex (End of the dec_hex function)

/*****************************************************************divide*********************************************************************/

      void divide(char cadena[])
      {
         int i,j;
         char parte1[6],parte2[6];
         parte1[0]='\0';
         parte2[0]='\0';
         parte1[0]='%';  //Para que extraer() lo detecte como binario (To extraer () trate him as binary)
         parte2[0]='%';  //Para que extraer() lo detecte como binario (To extraer () trate him as binary)
         i=1;
         j=0;
         while(i<5)  //Llenamos la primer parte (We fill the first part)
         {
            parte1[i]=cadena[j];
            i++;
            j++;
         }
         parte1[i]='\0';
         j=1;
         i--;
         while(i<8)  //Llenamos la segunda parte (We fill the second part)
         {
            parte2[j]=cadena[i];
            i++;
            j++;
         }
         parte2[j]='\0';
         i=extraer(parte1,-1);  //Se saca el valor numerico de la primera parte (We take the numerical value from the first part)
         dec_hex(i,0);  //Se saca en hexadecimal la primera parte (We get the first part in hexadecimal)
         strcpy(parte1,num_hex);
         parte1[1]='\0';
         j=extraer(parte2,-1);  //Se saca el valor numerico de la segunda parte (We take the numerical value from the second part)
         dec_hex(j,0);  //Se saca en hexadecimal la segunda parte (We get the second part in hexadecimal)
         strcpy(parte2,num_hex);
         parte2[1]='\0';
         xb[0]=parte1[0];
         xb[1]=parte2[0];
         xb[2]='\0';  //Aquí ya queda en xb la representación hexadecimal de 2 bytes del binario de 8 bits (Here we have in xb the hexadecimal representation in 2 bytes of the binary of 8 bits)
      }  //Fin de la función divide (End of the "divide function")

/*******************************************************************S19*********************************************************************/

      void S19(char registro[],char longitud[],char dir[],char datos[],char ch[])
      {
         int posicion=lseek(fd,0L,1),tipo;
         close(fd);
         if(strcmp(registro,"S0")==0)  //Cuando llegue el S0 sobreescribe (When the S0 comes we overwrite)
         {
            tipo=0;
            creat(nombre_S19,666);
         }
         if(strcmp(registro,"S1")==0)
            tipo=1;
         if(strcmp(registro,"S9")==0)
            tipo=9;
         fd=open(nombre_S19, 2);
         if(fd>0)
         {
            lseek(fd,0L,2);
            switch(tipo)
            {
               case 0:
                  write(fd,"S0",2);
                  write(fd,longitud,strlen(longitud));
                  write(fd,"0000",4);
                  write(fd,datos,strlen(datos));
                  write(fd,ch,strlen(ch));
                  write(fd,"\n",1);
                  break;
               case 1:
                  write(fd,"S1",2);
                  write(fd,longitud,strlen(longitud));
                  write(fd,dir,strlen(dir));
                  write(fd,datos,strlen(datos));
                  write(fd,ch,strlen(ch));
                  write(fd,"\n",1);
                  break;
               case 9:
                  write(fd,"S9",2);
                  write(fd,"03",2);
                  write(fd,"0000",4);
                  write(fd,"FC",2);
                  break;
               default:
                  printf("Hello world\n\n");
            }
         }
         else
            errores(105);
         close(fd);
         fd=open(archivo,0);
         lseek(fd,posicion,0);
      }  //Fin de la función S19 (End of the S19 function)

/*****************************************************************checksum******************************************************************/

      void checksum(char longitud[], char direccion[],char datos[])
      {
         int i=0,suma=0;
         char num_temp[4];
         ch[0]='\0';
         num_temp[0]='$';
         suma=suma+extraer(longitud,16);  //Primero le sumamos lo que vale en decimal la longitud (sin while por que son 2 bytes directamente) (First we add the value in decimal from longitud)
         while(direccion[i]!='\0')
         {
            num_temp[1]=direccion[i];
            num_temp[2]=direccion[i+1];
            num_temp[3]='\0';
            suma=suma+extraer(num_temp,-1);
            i=i+2;
         }
         i=0;
         while(datos[i]!='\0')
         {
            num_temp[1]=datos[i];
            num_temp[2]=datos[i+1];
            num_temp[3]='\0';
            suma=suma+extraer(num_temp,-1);
            i=i+2;
         }
         suma=suma*(-1);
         dec_bin(suma,16);
         i=8;  //Aquí tomamos los ultimos 8 bits de binario y los ponemos en las primeras 8 posiciones de bits del mismo (Here we take the last 8 bits and put them in the firsts 8 positions)
         suma=0;  //Es como si lo cortaramos a la mitad y solo tomaramos la ultima mitad (la que nos interesa) (It´s like we cut in the middle and only take the last half)
         while(i<=15)
         {
            binario[suma]=binario[i];
            i++;
            suma++;
         }
         binario[suma]='\0';
         suma=extraer(binario,2)-1;  //Esto es para quitarle el complemento a 2 que le deja la función dec_bin() (This is to take off the complement to 2 that the function dec_bin () let him)
         dec_hex(suma,1);
         strcpy(ch,num_hex);
         ch[2]='\0';
      }

/*****************************************************************errores*******************************************************************/

      void errores(int numero)
      {
         if(paso_2==1)
         {
            if(numero!=1 && numero!=2 && numero!=3 && numero!=4 && numero!=14 && numero!=97 && numero!=99 && numero!=100 && numero!=102 && numero!=104)
               if(linea_error==0)
                  printf("XXXX\t%s\t%s\t%s\n\n",etiqueta,codop,operando);
            switch(numero)
            {
               case 1:
                    printf("  --- Error: Archivo vacio\n\n");
                    break;
               case 2:
                    printf("COMENTARIO\n\n  --- Error: Comentario con mas de un signo \";\"\n\n");
                    break;
               case 3:
                    printf("COMENTARIO\n\n  --- Error: Comentario mayor a 80 caracteres\n\n");
                    break;
               case 4:
                    printf("COMENTARIO\n\n");
                    break;
               case 5:
                    printf("  --- Error: \"%c\" es un caracter de inicio invalido para las etiquetas\n\n",etiqueta[0]);
                    break;
               case 6:
                    printf("  --- Error: Caracter de inicio repetido en la etiqueta\n\n");
                    break;
               case 7:
                    printf("  --- Error: \"%c\" es un caracter invalido para una etiqueta\n\n",letra3);
                    break;
               case 8:
                    printf("  --- Error: Etiqueta mayor a 8 caracteres\n\n");
                    break;
               case 9:
                    printf("  --- Error: \"%c\" es un caracter de inicio invalido para un codop\n\n",codop[0]);
                    break;
               case 10:
                    printf("  --- Error: \"%c\" es un caracter invalido para un codop\n\n",letra3);
                    break;
               case 11:
                    printf("  --- Error: Mas de un caracter \".\" en el codop\n\n",codop);
                    break;
               case 12:
                    printf("  --- Error: Codigo de operacion mayor a 5 caracteres\n\n");
                    break;
               case 13:
                    printf("  --- Error: No hay codigo de operacion\n\n");
                    break;
               case 14:
                    printf("  --- Error: El archivo termino y no se encontro la directiva END\n\n");
                    break;
               case 15:
                    printf("  --- Error: %s no se encuentra en el TABOP.txt y no es una directiva\n\n",codop);
                    break;
               case 16:
                    printf("  --- Error: El codigo de operacion %s debe llevar operando\n\n",codop);
                    break;
               case 17:
                    printf("  --- Error: El codigo de operacion %s no debe llevar operando\n\n",codop);
                    break;
               case 18:
                    printf("  --- Error: De acuerdo al TABOP el codigo de operacion no admite este modo \n             de direccionamiento\n\n");
                    break;
               case 19:
                    printf("  --- Error: Formato invalido para el modo inmediato\n\n");
                    break;
               case 20:
                    printf("  --- Error: Formato invalido para el modo inmediato en base 16 \n\n");
                    break;
               case 21:
                    printf("  --- Error: Caracter invalido para los numeros de base 16\n\n");
                    break;
               case 22:
                    printf("  --- Error: Formato invalido para el modo inmediato en base 8 \n\n");
                    break;
               case 23:
                    printf("  --- Error: Caracter invalido para los numeros de base 8\n\n");
                    break;
               case 24:
                    printf("  --- Error: Formato invalido para el modo inmediato en base 2 \n\n");
                    break;
               case 25:
                    printf("  --- Error: Caracter invalido para los numeros de base 2\n\n");
                    break;
               case 26:
                    printf("  --- Error: Caracter invalido para los numeros de base 10\n\n");
                    break;
               case 27:
                    printf("  --- Error: El modo de direccionamiento inmediato no admite este caracter\n              \" %c \" despues del #\n\n",operando[1]);
                    break;
               case 28:
                    printf("  --- Error: Valor de rango invalido para el modo de direccionamiento\n              inmediato\n\n");
                    break;
               case 29:
                    printf("  --- Error: Formato invalido para la base hexadecimal\n\n");
                    break;
               case 30:
                    printf("  --- Error: Caracter invalido para los numeros de base 16\n\n");
                    break;
               case 31:
                    printf("  --- Error: Valor de rango invalido para el modo de direccionamiento directo\n             o extendido\n\n");
                    break;
               case 32:
                    printf("  --- Error: Formato invalido para la base octal\n\n");
                    break;
               case 33:
                    printf("  --- Error: Caracter invalido para los numeros de base 8\n\n");
                    break;
               case 34:
                    printf("  --- Error: Valor de rango invalido para el modo de direccionamiento directo\n             o extendido\n\n");
                    break;
               case 35:
                    printf("  --- Error: Formato invalido para la base binaria\n\n");
                    break;
               case 36:
                    printf("  --- Error: Caracter invalido para los numeros de base 2\n\n");
                    break;
               case 37:
                    printf("  --- Error: Valor de rango invalido para el modo de direccionamiento directo\n             o extendido\n\n");
                    break;
               case 38:
                    printf("  --- Error: Digito invalido para los numeros de base 10\n\n");
                    break;
               case 39:
                    printf("  --- Error: No se introdujo ningun digito antes de la coma\n\n");
                    break;
               case 40:
                    printf("  --- Error: Formato invalido para numeros decimales\n\n");
                    break;
               case 41:
                    printf("  --- Error: No se introdujo ningun registro\n\n");
                    break;
               case 42:
                    printf("  --- Error: Mas de un signo en el numero\n\n");
                    break;
               case 43:
                    printf("  --- Error: Valor de rango invalido para el modo de direccionamiento directo\n             o extendido\n\n");
                    break;
               case 44:
                    printf("  --- Error: Nombre de registro invalido\n\n");
                    break;
               case 45:
                    printf("  --- Error: Valor de rango invalido para los modos indizados de 5, 9 y 16 \n             bits\n\n");
                    break;
               case 46:
                    printf("  --- Error: Valor de rango invalido para algun modo de auto pre/post \n             decremento/incremento\n\n");
                    break;
               case 47:
                    printf("  --- Error: Formato invalido al usar el corchete\n\n");
                    break;
               case 48:
                    printf("  --- Error: Entre la coma y el corchete no existe instruccion\n\n");
                    break;
               case 49:
                    printf("  --- Error: Instruccion vacia entre corchetes\n\n");
                    break;
               case 50:
                    printf("  --- Error: Registro invalido para el modo de acumulador indirecto\n\n");
                    break;
               case 51:
                    printf("  --- Error: Registro invalido para el modo de acumulador indirecto\n\n");
                    break;
               case 52:
                    printf("  --- Error: Registro invalido para el modo de acumulador indirecto\n\n");
                    break;
               case 53:
                    printf("  --- Error: Falta el cierre del corchete\n\n");
                    break;
               case 54:
                    printf("  --- Error: No se encontro coma y se trata de un modo indizado\n\n");
                    break;
               case 55:
                    printf("  --- Error: Formato o registro invalido para el modo de acumulador \n             indirecto\n\n");
                    break;
               case 56:
                    printf("  --- Error: Caracter invalido para los numeros decimales\n\n");
                    break;
               case 57:
                    printf("  --- Error: Uso invalido del caracter \" - \" en los numeros decimales\n\n");
                    break;
               case 58:
                    printf("  --- Error: Formato o registro invalido para el modo indirecto de 16 bits e \n             indizado de acumulador\n      indirecto\n\n");
                    break;
               case 59:
                    printf("  --- Error: Falta el cierre del corchete\n\n");
                    break;
               case 60:
                    printf("  --- Error: Rango invalido para el modo indizado indirecto de 16 bits\n\n");
                    break;
               case 61:
                    printf("  --- Error: No se encontro coma y se trata de un modo indizado\n\n");
                    break;
               case 62:
                    printf("  --- Error: Falta el cierre del corchete\n\n");
                    break;
               case 63:
                    printf("  --- Error: Caracter \" %c \" invalido para los modos indizados de acumulador\n              indirecto e indirecto\n      de 16 bits\n\n",operando[1]);
                    break;
               case 64:
                    printf("  --- Error: Falta el cierre del corchete\n\n");
                    break;
               case 65:
                    printf("  --- Error: No se encontro coma y se trata de un modo indizado\n\n");
                    break;
               case 66:
                    printf("  --- Error: Formato o registro invalido para el modo indizado de acumulador\n\n");
                    break;
               case 67:
                    printf("  --- Error: Caracter, invalido para una etiqueta\n\n");
                    break;
               case 68:
                    printf("  --- Error: Caracter de inicio repetido para ser una etiqueta\n\n");
                    break;
               case 69:
                    printf("  --- Error: Longitud de etiqueta invalida\n\n");
                    break;
               case 70:
                    printf("  --- Error: Cantidad de comas invalidas\n\n");
                    break;
               case 71:
                    printf("  --- Error: Formato invalido para cualquier  modo indizado\n\n");
                    break;
               case 72:
                    printf("  --- Error: Formato o registro invalido para el modo indizado de 5 bits\n\n");
                    break;
               case 73:
                    printf("  --- Error: Operando invalido\n\n");
                    break;
               case 74:
                    printf("  --- Error: Solo puede haber una directiva %s y ya van %d\n\n",codop,org);
                    break;
               case 75:
                    printf("  --- Error: La directiva %s no soporta etiquetas\n\n",codop);
                    break;
               case 76:
                    printf("  --- Error: Formato invalido para un numero de base 16 \n\n");
                    break;
               case 77:
                    printf("  --- Error: Caracter invalido para los numeros de base 16\n\n");
                    break;
               case 78:
                    printf("  --- Error: Formato invalido para un numero en base 8 \n\n");
                    break;
               case 79:
                    printf("  --- Error: Caracter invalido para los numeros de base 8\n\n");
                    break;
               case 80:
                    printf("  --- Error: Formato invalido para un numero de base 2 \n\n");
                    break;
               case 81:
                    printf("  --- Error: Caracter invalido para los numeros de base 2\n\n");
                    break;
               case 82:
                    printf("  --- Error: Caracter invalido para los numeros de base 10\n\n");
                    break;
               case 83:
                    printf("  --- Error: Mas de un signo de menos\n\n");
                    break;
               case 84:
                    printf("  --- Error: Formato invalido para un numero de base 10\n\n");
                    break;
               case 85:
                    printf("  --- Error: La directiva %s debe llevar operando\n\n",codop);
                    break;
               case 86:
                    printf("  --- Error: La directiva %s debe llevar etiqueta\n\n",codop);
                    break;
               case 87:
                    printf("  --- Error: Rango invalido para la directiva %s\n\n",codop);
                    break;
               case 88:
                    printf("  --- Error: La directiva %s debe llevar etiqueta\n\n",codop);
                    break;
               case 89:
                    printf("  --- Error: La directiva %s debe llevar etiqueta\n\n",codop);
                    break;
               case 90:
                    printf("  --- Error: Rango invalido para la directiva %s\n\n",codop);
                    break;
               case 91:
                    printf("  --- Error: La directiva %s requiere un operando numerico\n\n",codop);
                    break;
               case 92:
                    printf("  --- Error: La directiva %s debe llevar etiqueta\n\n",codop);
                    break;
               case 93:
                    printf("  --- Error: Mas de un signo de comillas dobles\n\n");
                    break;
               case 94:
                    printf("  --- Error: Falta el cierre de las comillas\n\n");
                    break;
               case 95:
                    printf("  --- Error: La directiva %s debe llevar operando\n\n",codop);
                    break;
               case 96:
                    printf("  --- Error: Operando invalido para la directiva %s\n\n",codop);
                    break;
               case 97:
                    printf("\n  --- Se a encontrado la directiva %s por lo que aqui termina el programa ---\n\n",codop);
                    break;
               case 98:
                    printf("  --- Error: La directiva %s no soporta operandos\n\n",codop);
                    break;
               case 99:
                    printf("  --- Error: Intentando crear el archivo TEMPORAL\n\n");
                    break;
               case 100:
                    printf("  --- Error: El CONTLOC esta fuera de rango\n\n");
                    break;
               case 101:
                    printf("  --- Error: Antes del ORG solo puede haber directivas EQU\n\n");
                    break;
               case 102:
                    printf("  --- Error: Intentando crear el archivo TABSIM.txt\n\n");
                    break;
               case 103:
                    printf("  --- Error: Etiqueta %s duplicada, no se pudo introducir en el TABSIM.txt\n\n",etiqueta);
                    break;
               case 104:
                    printf("\n\n  --- Error: Valor de desplazamiento invalido");
                    break;
               case 105:
                    printf("  --- Error: Intentando crear el archivo S19\n\n");
                    break;
               default:
                    printf("  --- Error: default");
            }  //Fin del switch (End of the switch)
            if(numero!=1 && numero!=2 && numero!=3 && numero!=4 && numero!=14 && numero!=97 && numero!=99 && numero!=100 && numero!=102 && numero!=104)
               linea_error++;
         }  //Fin del si es el paso 2 de ensamblaje (End of the "if it´s the second step of assembly")
      }  //Fin de la función errores (End of the errores function)
};

/*******************************************************************************************************************************************/
/*****************************************************************Main**********************************************************************/
/*******************************************************************************************************************************************/

int main(int argc, char *argv[])
{
    char seguir;
    clasearchivo archivo;
    do
    {
       system("CLS");
       archivo.abrir();
       if(archivo.abierto==1)
       {
          printf("----------------------------------------------------------------------");
          printf("\nBueno, esto es lo que contiene el archivo:\n\n\n");
          lseek(archivo.fd,0L,0);
          archivo.paso_2=0;
          archivo.mostrar();
       }
       close(archivo.fd);
       do
       {
          printf("\n\nQuieres abrir otro archivo S/N: ");
          fflush(stdin);
          scanf("%c",&seguir);
          if(seguir!='n' && seguir!='N' && seguir!='s' && seguir!='S')
             printf("\n\n  --- Error: Tecla invalida, por favor presione una tecla valida (S/N)\n");
       }while(seguir!='n' && seguir!='N' && seguir!='s' && seguir!='S');
    }while(seguir=='S' || seguir=='s');
    printf("\n\n");
    system("PAUSE");
    return EXIT_SUCCESS;
}
