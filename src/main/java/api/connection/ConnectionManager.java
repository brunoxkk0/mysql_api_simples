package api.connection;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import api.connection.impl.pools.SqliteConnection;
import api.core.AsyncManager;
import api.exception.connection.ConnectionNotEstablishedException;
import api.exception.connection.DuplicateConnectionNameException;
import api.interfaces.actions.ITransaction;
import api.interfaces.connection.IConnection;
import api.interfaces.connection.IConnectionManager;
import api.models.Transaction;
import api.models.atributes.ForeignKey;
import api.models.atributes.PrimaryKey;
import api.models.enums.ConnectionType;
import api.models.utils.Checkers;

public class ConnectionManager implements IConnectionManager{

	private static final AsyncManager asyncManager = new AsyncManager(16);

	public AsyncManager getAsyncManager() {
		return asyncManager;
	}

	private String name;
	private boolean firstRun = true;

	private IConnection<?> primaryConnection = null;

	private IConnection<?> logConnection = null;

	private ConnectionType connectionType;

	private List<IConnection<?>> list = new ArrayList<>();
	private List<IConnection<?>> listException = new ArrayList<>();
	
	

	public ConnectionManager(String name) {
	    Checkers.validateStringNotNull(name,"name");
		this.name = name;
	}

	private Connection testConnection() throws ConnectionNotEstablishedException {
		System.out.println("Checkando lista de conexão");
		if (list.size() == 0) {

			throw new NullPointerException("Not exists database connections register");
		}
		System.out.println("--------");
		Connection connection = null;
		System.out.println("*****");
		for (IConnection<?> iConnection : list) {

			connection = iConnection.openConnection();
			System.out.println("Loop");
			if (connection != null) {

				primaryConnection = iConnection;
				connectionType = primaryConnection.getConnectionType();
				firstRun = false;
				break;

			}
			if (listException.contains(iConnection)) {
				continue;
			}
			System.out.println("[API-MSQL]Connection " + iConnection.getConnectionType() + " not estabilized");
			System.out.println("Try next connection");
			listException.add(iConnection);

		}
		System.out.println("Checkando principal");
		if (primaryConnection == null) {
			System.out.println("000000");
			throw new ConnectionNotEstablishedException();
		}
		
		if (firstRun) {
			System.out.println("rodou primeira vez");
			return connection;
		}

		logConnection = new SqliteConnection("log",name+"_log");
		System.out.println("Tabelas");
		createTablesLogs();
		System.out.println("Conn");
		return connection;

	}

	private void createTablesLogs() {

		ITransaction transaction = new Transaction();
		transaction.create().setTable("transactions").setConnection(logConnection)
				.addColumn("id", "INTEGER", new PrimaryKey().addAutoIncrement()).addColumn("sql", "TEXT NOT NULL")
				.addColumn("transaction_type",
						"TEXT CHECK(\"transaction_type\" IN ('INSERT', 'SELECT', 'UPDATE', 'DELETE', 'CREATE_TABLE', 'TRUNCATE')) NOT NULL")
				.addColumn("milliseconds", "REAL NOT NULL").commit();

		// transaction.createTable("CREATE TABLE IF NOT EXISTS \"transactions\" (\"id\"
		// INTEGER,\"sql\" TEXT NOT NULL,\"transaction_type\" TEXT
		// CHECK(\"transaction_type\" IN ('INSERT', 'SELECT', 'UPDATE', 'DELETE',
		// 'CREATE_TABLE', 'TRUNCATE')) NOT NULL, \"milliseconds\" REAL NOT NULL,PRIMARY
		// KEY(\"id\" AUTOINCREMENT))",logConnection);
		transaction.create().setTable("transactions_values").setConnection(logConnection)
				.addColumn("id", "INTEGER", new PrimaryKey().addAutoIncrement()).addColumn("field", "text")
				.addColumn("transactionID", "INTEGER",
						new ForeignKey().setTableForeign("transactions").setColumnForeign("id"))
				.addColumn("parameterOrder", "INTEGER").commit();
		// transaction.createTable("CREATE TABLE IF NOT EXISTS \"transactions_values\"
		// (\"id\" INTEGER,\"transactionId\" INTEGER,\"parameterOrder\"
		// INTEGER,\"field\" TEXT, PRIMARY KEY(\"Id\" AUTOINCREMENT),FOREIGN
		// KEY(\"TransactionId\") REFERENCES \"transactions\"(\"id\"))",logConnection);
		transaction.create().setTable("lastest_transaction").setConnection(logConnection)
				.addColumn("id", "INTEGER", new PrimaryKey().addAutoIncrement())
				.addColumn("transactionID", "INTEGER",
						new ForeignKey().setTableForeign("transactions").setColumnForeign("id"))
				.addColumn("milliseconds", "REAL").commit();
		// transaction.createTable("CREATE TABLE IF NOT EXISTS \"lastest_transaction\" (
		// \"id\" INTEGER,\"transactionId\" INTEGER,\"milliseconds\" REAL,PRIMARY
		// KEY(\"id\" AUTOINCREMENT),FOREIGN KEY(\"transactionId\") REFERENCES
		// \"transactions\"(\"id\"))",logConnection);
	

	}

	public Connection getConnection() {
		System.out.println("TESTE");
		if (primaryConnection != null) {
			System.out.println("1");
			Connection connection = primaryConnection.openConnection();
			if (connection != null) {
				return connection;
			}
			System.out.println("----");

		}
		try {

			return testConnection();

		} catch (ConnectionNotEstablishedException e) {

			e.printStackTrace();
		}

		return primaryConnection.openConnection();
	}

	
	public IConnection<?> getConnectionByName(String name) {
		Checkers.validateStringNotNull(name, "name");
		if (list.size() == 0) {

			throw new NullPointerException("Not exists database connections register");
		}
		try {
		
			Optional<IConnection<?>>Iconnection = list.stream().filter(
					connectionObj -> connectionObj.getName().toLowerCase().equalsIgnoreCase(name)
					).findFirst();
			return Iconnection.get();
		
		}catch (Exception e) {
		
			 if(!e.getMessage().equals("No value present")) {
	    		  e.printStackTrace();
	    	  }
			return null;
		}
		
	}

	public void closeConnection() {

		if (primaryConnection != null) {
			primaryConnection.closeConnection(getConnection());
		}
	}

	public void addConnection(IConnection<?> connection) {
		
      Checkers.validadeObjectNotNull(connection,"connection");
      try {
    	  Optional<IConnection<?>>Iconnection = list.stream().filter(
  				connectionObj -> connectionObj.getName().toLowerCase().equalsIgnoreCase(connection.getName())
  				).findFirst();
  		
  		Iconnection.get();
  		throw new DuplicateConnectionNameException();
  		 
      }catch (Exception e) {
    	  
    	  if(!e.getMessage().equals("No value present")) {
    		  e.printStackTrace();
    		  return;
    	  }
    	 
		 
	}
		
		list.add(connection);

	}

	public void removeConnection(String connectionName) {
         Checkers.validateStringNotNull(connectionName, "connectionName");
		if (list.size() == 0) {
			return;
		}

		list.removeIf(connection -> connection.getName().equals(connectionName.toLowerCase()));
		
	}

	public ConnectionType getConnectionType() {
		return connectionType;
	}

	public Connection getLogConnecton() {

		if (logConnection == null) {
			logConnection = new SqliteConnection("log","log.db");
		}
		try {
			testConnection();
		} catch (ConnectionNotEstablishedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return logConnection.openConnection();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
