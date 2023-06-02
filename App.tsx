import React, {useState} from 'react';
import {View, Button, Text} from 'react-native';
import {
  initiatePurchase,
  getPaymentResponses,
} from './PaymentNative/InAppPurchaseModule';

const App = () => {
  const [paymentStatus, setPaymentStatus] = useState([]);

  const handlePurchase = () => {
    initiatePurchase('Google_Play_Key');
  };

  const handleGetResponses = async () => {
    const responses = await getPaymentResponses();
    console.log('responses ===>>', responses);
    setPaymentStatus(responses.join('\n ->'));
  };

  return (
    <View>
      <Button title="Buy" onPress={handlePurchase} />
      <Button title="Get Payment Responses" onPress={handleGetResponses} />
      <Text>{paymentStatus}</Text>
    </View>
  );
};

export default App;
