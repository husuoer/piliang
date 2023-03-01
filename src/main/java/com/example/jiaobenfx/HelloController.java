package com.example.jiaobenfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;

import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.ExecutionException;


public class HelloController {
    @FXML
    private TextField coinAddress;
    @FXML
    private TextField pk;
    @FXML
    private Button stopMine;
    @FXML
    private TextArea mineLog;
    @FXML
    private TextField toAddress;
    @FXML
    private TextField amount;



    private Timer timer;
    @FXML
    public void startMineEvent(ActionEvent event) throws ExecutionException, InterruptedException {
        Credentials credentials = Credentials.create(pk.getText());
        String adds= toAddress.getText();
        String[] addressArray = adds.split("/");


        String fromAddress = credentials.getAddress();
        Web3j web3 = Web3j.build(new HttpService("https://bsc-dataseed1.binance.org"));
        EthGetTransactionCount ethGetTransactionCount = null;
        try {
            ethGetTransactionCount = web3.ethGetTransactionCount(
                    fromAddress, DefaultBlockParameterName.LATEST).sendAsync().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        BigInteger no = ethGetTransactionCount.getTransactionCount();

        Thread thread = new Thread(new Runnable() {
            public void run() {
                for (int i = 0;i<addressArray.length;i++) {

                    BigInteger nonce = no.add(new BigInteger(i+""));
                    Address address = new Address(addressArray[i]);
//            Uint256 value = new Uint256(new BigInteger(amount.getText()).multiply(new BigInteger("100000000")));
                    BigDecimal BV = new BigDecimal(amount.getText());
                    long lv = BV.multiply(new BigDecimal("1000000000000000000")).longValue();
                    Uint256 value = new Uint256(BigInteger.valueOf(lv));
                    List<Type> parametersList = new ArrayList<>();
                    parametersList.add(address);
                    parametersList.add(value);
                    List<TypeReference<?>> outList = new ArrayList<>();
                    Function function = new Function("transfer", parametersList, outList);
                    String encodedFunction = FunctionEncoder.encode(function);
                    System.out.println(DefaultGasProvider.GAS_PRICE);
                    System.out.println(DefaultGasProvider.GAS_LIMIT);
                    RawTransaction rawTransaction = RawTransaction.createTransaction(nonce, BigInteger.valueOf(5000000000L),
                            new BigInteger("70000"), coinAddress.getText(), encodedFunction);
                    byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                    String hexValue = Numeric.toHexString(signedMessage);
                    EthSendTransaction ethSendTransaction = null;
                    try {
                        ethSendTransaction = web3.ethSendRawTransaction(hexValue).sendAsync().get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                    Object transactionHash = ethSendTransaction.getTransactionHash();
                    while (transactionHash==null){
                        try {
                            ethSendTransaction = web3.ethSendRawTransaction(hexValue).sendAsync().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                    mineLog.appendText("转账成功，转账Hash：" + transactionHash.toString() + "\n");

                }
            }
        });
        thread.start();

//        mintTask.status = true;
//        timer = new Timer();
//        timer.schedule(mintTask,10000L,90000L);
//        mineLog.appendText("开始挖矿"+"\n");
    }



}