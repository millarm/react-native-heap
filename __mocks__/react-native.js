const reactNative = jest.genMockFromModule('react-native');

reactNative.NativeModules = {
  RNHeap: {
    track: jest.fn(),
    setAppId: jest.fn(),
    identify: jest.fn(),
    resetIdentity: jest.fn(),
    addUserProperties: jest.fn(),
    addEventProperties: jest.fn(),
    removeEventProperty: jest.fn(),
    clearEventProperties: jest.fn(),
  },
};

module.exports = reactNative;
