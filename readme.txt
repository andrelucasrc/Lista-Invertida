Autor: André Lucas Ribeiro Costa.
Desenvolvido durante o 3º período de Ciência da computação,
para a disciplina de Algoritmos e Estrutura de Dados 3.

O que é uma lista invertida?
Trata-se de uma estrutura que agiliza a busca por termos dentro de uma
estrutura qualquer, neste caso foi utilizados nomes.

Informações sobre a implementação:
Para este projeto foram utilizados um CRUD para o armazenamento dos dados
inseridos pelo usuário na estrutura, além de claro um lista invertida, que
conta com 2 arquivos de indíces, um para salvar os termos e as posições 
iniciais dos ids, e o segundo para salvar os ids dos nomes que possuem
tais termos.

CRUD:
Create Read Update Delete, um banco de dados simples com as quatro operações
listadas anteriormente, para mais explicações, basta verificar o código-fonte
dentro do arquivo, nomeado CRUD.java, na pasta BancoDeDados.
Para salvar um novo dado dentro do CRUD primeiro deve criar um entidade que
segue o molde da Estrutura Registro.java na pasta Props.

Lista invertida:
 Trata-se de um arquivo que gerencia os dois registros abaixo.

 Para um melhor detalhamento visualizar o código-fonte do arquivo Lista.java,
 na pasta Banco de Dados.

 - Termos:
		Trata-se de um arquivo indice contendo o termo e o endereço de seu
		primeiro bucket dentro da estrutura de ids. O arquivo é lido da
		seguinte forma: String UTF-8, long Posição. 
		
		Para mais detalhes visualizar o código-fonte do arquivo Termos.java, 
		na pasta BancoDeDados.

 - IDS:
		Trata-se de um arquivo contendo todos os ids de nomes que possuem o
		termo do bucket, sem o auxílio da estrtura de Termos o arquivo de 
		IDS não faz muito sentido, pois seria apenas um monte de números
		sem muito sentido. O arquivo é lido da seguinte forma: short
		quantidade de termos do bucket, 10x int id do termo inserido
		o buckete preenche os valores que não existem com -1, para alocar
		o espaço necessário para o bucket ter sempre o tamanho necessário,
		long endereço do próximo bucket relacionado ao termo, caso não
		exista próximo termo valor salvo será -1.

		Para mais detalhes visualisar o código-fonte do arquivo
		Identificacao.java dentro da pasta BancoDeDados.

Utils:
 Trata-se de um arquivo que auxília a execução de tarefas que são utilizadas
 com muita frequência nas entidades listadas anteriormente. 
 Para visualizar alguma de suas operações basta verificar o arquivo Utils.java
 na pasta Utils.

Informações adicionais:
	A estrutura foi testada no Linux, e algumas de sua funções não são compatíveis
	com outros sistemas, podendo ocasionar algum erro durante sua execução.
