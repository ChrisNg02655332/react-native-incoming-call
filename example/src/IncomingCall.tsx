import React from 'react';
import { View, Text, Button } from 'react-native';
import RNIncomingCall from '../../src/index';

export { IncomingCall };

function IncomingCall(props) {
  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Text>Incoming Call: {props.uuid}</Text>

      <View style={{ flexDirection: 'row', gap: 15 }}>
        <Button
          title="Denied"
          onPress={() => RNIncomingCall.declineCall(props.uuid, props.payload)}
        />

        <Button
          title="Answer"
          onPress={() => {
            RNIncomingCall.answerCall(props.uuid, props.payload);
          }}
        />
      </View>
    </View>
  );
}
