# Table of contents

* [API Mysql/Sqlite](README.md)
  * [Classes](doc/classes/README.md)
    * [ConnectionManager](doc/classes/connectionmanager.md)
    * [IConnection](readme/classes/iconnection.md)
    * [Conectores](readme/classes/conectores/README.md)
      * [MysqlConnection](readme/classes/conectores/mysqlconnection.md)
      * [SqliteConnection](readme/classes/conectores/mysqlconnection-1.md)
    * [Transaction](readme/classes/transaction.md)
    * [ICreateAtribute](readme/classes/icreateatribute/README.md)
      * [PrimaryKey](readme/classes/icreateatribute/primarykey.md)
      * [ForeignKey](readme/classes/icreateatribute/foreignkey.md)
      * [Unique](readme/classes/icreateatribute/unique.md)
      * [NotNull](readme/classes/icreateatribute/notnull.md)
    * [Ações](readme/classes/acoes/README.md)
      * [ICreate](readme/classes/acoes/icreate.md)
      * [IInsert](readme/classes/acoes/iinsert.md)
      * [ISelect](readme/classes/acoes/iselect.md)
      * [IUpdate](readme/classes/acoes/iupdate.md)
      * [IDelete](readme/classes/acoes/idelete.md)
    * [ICommitAction](readme/classes/icommitaction.md)
  * [Enums](doc/enums.md)
    * [TransactionType](doc/enums/transactiontype.md)
    * [ConnectionType](doc/enums/connectiontype.md)
    * [CreateAtributes](doc/enums/createatributes.md)
  * [Exemplos](readme/exemplos/README.md)
    * [Criando o ConnectionManager](readme/exemplos/criando-o-connectionmanager.md)
    * [Transaçoes](readme/exemplos/transacoes/README.md)
      * [Criar Tabela](readme/exemplos/transacoes/criar-tabela.md)
      * [Inserir Dados Na Tabela](readme/exemplos/transacoes/inserir-dados-na-tabela.md)
      * [Consultando Dados na Tabela](readme/exemplos/transacoes/consultando-dados-na-tabela.md)
