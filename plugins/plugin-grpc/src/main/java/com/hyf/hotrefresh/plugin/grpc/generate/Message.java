// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: remoting_api.proto

package com.hyf.hotrefresh.plugin.grpc.generate;

/**
 * Protobuf type {@code hotrefresh.v1.Message}
 */
public final class Message extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:hotrefresh.v1.Message)
    MessageOrBuilder {
private static final long serialVersionUID = 0L;
  // Use Message.newBuilder() to construct.
  private Message(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Message() {
    encoding_ = com.google.protobuf.ByteString.EMPTY;
    codec_ = com.google.protobuf.ByteString.EMPTY;
    compress_ = com.google.protobuf.ByteString.EMPTY;
    messageType_ = com.google.protobuf.ByteString.EMPTY;
  }

  @Override
  @SuppressWarnings({"unused"})
  protected Object newInstance(
      UnusedPrivateParameter unused) {
    return new Message();
  }

  @Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private Message(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    if (extensionRegistry == null) {
      throw new NullPointerException();
    }
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          case 8: {

            id_ = input.readInt32();
            break;
          }
          case 18: {

            encoding_ = input.readBytes();
            break;
          }
          case 26: {

            codec_ = input.readBytes();
            break;
          }
          case 34: {

            compress_ = input.readBytes();
            break;
          }
          case 42: {

            messageType_ = input.readBytes();
            break;
          }
          case 50: {
            if (!((mutable_bitField0_ & 0x00000001) != 0)) {
              metadata_ = com.google.protobuf.MapField.newMapField(
                  MetadataDefaultEntryHolder.defaultEntry);
              mutable_bitField0_ |= 0x00000001;
            }
            com.google.protobuf.MapEntry<String, String>
            metadata__ = input.readMessage(
                MetadataDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
            metadata_.getMutableMap().put(
                metadata__.getKey(), metadata__.getValue());
            break;
          }
          case 58: {
            com.google.protobuf.Any.Builder subBuilder = null;
            if (body_ != null) {
              subBuilder = body_.toBuilder();
            }
            body_ = input.readMessage(com.google.protobuf.Any.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(body_);
              body_ = subBuilder.buildPartial();
            }

            break;
          }
          default: {
            if (!parseUnknownField(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return RemotingApiProto.internal_static_hotrefresh_v1_Message_descriptor;
  }

  @SuppressWarnings({"rawtypes"})
  @Override
  protected com.google.protobuf.MapField internalGetMapField(
      int number) {
    switch (number) {
      case 6:
        return internalGetMetadata();
      default:
        throw new RuntimeException(
            "Invalid map field number: " + number);
    }
  }
  @Override
  protected FieldAccessorTable
      internalGetFieldAccessorTable() {
    return RemotingApiProto.internal_static_hotrefresh_v1_Message_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            Message.class, Builder.class);
  }

  public static final int ID_FIELD_NUMBER = 1;
  private int id_;
  /**
   * <code>int32 id = 1;</code>
   * @return The id.
   */
  @Override
  public int getId() {
    return id_;
  }

  public static final int ENCODING_FIELD_NUMBER = 2;
  private com.google.protobuf.ByteString encoding_;
  /**
   * <code>bytes encoding = 2;</code>
   * @return The encoding.
   */
  @Override
  public com.google.protobuf.ByteString getEncoding() {
    return encoding_;
  }

  public static final int CODEC_FIELD_NUMBER = 3;
  private com.google.protobuf.ByteString codec_;
  /**
   * <code>bytes codec = 3;</code>
   * @return The codec.
   */
  @Override
  public com.google.protobuf.ByteString getCodec() {
    return codec_;
  }

  public static final int COMPRESS_FIELD_NUMBER = 4;
  private com.google.protobuf.ByteString compress_;
  /**
   * <code>bytes compress = 4;</code>
   * @return The compress.
   */
  @Override
  public com.google.protobuf.ByteString getCompress() {
    return compress_;
  }

  public static final int MESSAGETYPE_FIELD_NUMBER = 5;
  private com.google.protobuf.ByteString messageType_;
  /**
   * <code>bytes messageType = 5;</code>
   * @return The messageType.
   */
  @Override
  public com.google.protobuf.ByteString getMessageType() {
    return messageType_;
  }

  public static final int METADATA_FIELD_NUMBER = 6;
  private static final class MetadataDefaultEntryHolder {
    static final com.google.protobuf.MapEntry<
        String, String> defaultEntry =
            com.google.protobuf.MapEntry
            .<String, String>newDefaultInstance(
                RemotingApiProto.internal_static_hotrefresh_v1_Message_MetadataEntry_descriptor,
                com.google.protobuf.WireFormat.FieldType.STRING,
                "",
                com.google.protobuf.WireFormat.FieldType.STRING,
                "");
  }
  private com.google.protobuf.MapField<
      String, String> metadata_;
  private com.google.protobuf.MapField<String, String>
  internalGetMetadata() {
    if (metadata_ == null) {
      return com.google.protobuf.MapField.emptyMapField(
          MetadataDefaultEntryHolder.defaultEntry);
    }
    return metadata_;
  }

  public int getMetadataCount() {
    return internalGetMetadata().getMap().size();
  }
  /**
   * <code>map&lt;string, string&gt; metadata = 6;</code>
   */

  @Override
  public boolean containsMetadata(
      String key) {
    if (key == null) { throw new NullPointerException(); }
    return internalGetMetadata().getMap().containsKey(key);
  }
  /**
   * Use {@link #getMetadataMap()} instead.
   */
  @Override
  @Deprecated
  public java.util.Map<String, String> getMetadata() {
    return getMetadataMap();
  }
  /**
   * <code>map&lt;string, string&gt; metadata = 6;</code>
   */
  @Override

  public java.util.Map<String, String> getMetadataMap() {
    return internalGetMetadata().getMap();
  }
  /**
   * <code>map&lt;string, string&gt; metadata = 6;</code>
   */
  @Override

  public String getMetadataOrDefault(
      String key,
      String defaultValue) {
    if (key == null) { throw new NullPointerException(); }
    java.util.Map<String, String> map =
        internalGetMetadata().getMap();
    return map.containsKey(key) ? map.get(key) : defaultValue;
  }
  /**
   * <code>map&lt;string, string&gt; metadata = 6;</code>
   */
  @Override

  public String getMetadataOrThrow(
      String key) {
    if (key == null) { throw new NullPointerException(); }
    java.util.Map<String, String> map =
        internalGetMetadata().getMap();
    if (!map.containsKey(key)) {
      throw new IllegalArgumentException();
    }
    return map.get(key);
  }

  public static final int BODY_FIELD_NUMBER = 7;
  private com.google.protobuf.Any body_;
  /**
   * <code>.google.protobuf.Any body = 7;</code>
   * @return Whether the body field is set.
   */
  @Override
  public boolean hasBody() {
    return body_ != null;
  }
  /**
   * <code>.google.protobuf.Any body = 7;</code>
   * @return The body.
   */
  @Override
  public com.google.protobuf.Any getBody() {
    return body_ == null ? com.google.protobuf.Any.getDefaultInstance() : body_;
  }
  /**
   * <code>.google.protobuf.Any body = 7;</code>
   */
  @Override
  public com.google.protobuf.AnyOrBuilder getBodyOrBuilder() {
    return getBody();
  }

  private byte memoizedIsInitialized = -1;
  @Override
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  @Override
  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (id_ != 0) {
      output.writeInt32(1, id_);
    }
    if (!encoding_.isEmpty()) {
      output.writeBytes(2, encoding_);
    }
    if (!codec_.isEmpty()) {
      output.writeBytes(3, codec_);
    }
    if (!compress_.isEmpty()) {
      output.writeBytes(4, compress_);
    }
    if (!messageType_.isEmpty()) {
      output.writeBytes(5, messageType_);
    }
    com.google.protobuf.GeneratedMessageV3
      .serializeStringMapTo(
        output,
        internalGetMetadata(),
        MetadataDefaultEntryHolder.defaultEntry,
        6);
    if (body_ != null) {
      output.writeMessage(7, getBody());
    }
    unknownFields.writeTo(output);
  }

  @Override
  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (id_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(1, id_);
    }
    if (!encoding_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(2, encoding_);
    }
    if (!codec_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(3, codec_);
    }
    if (!compress_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(4, compress_);
    }
    if (!messageType_.isEmpty()) {
      size += com.google.protobuf.CodedOutputStream
        .computeBytesSize(5, messageType_);
    }
    for (java.util.Map.Entry<String, String> entry
         : internalGetMetadata().getMap().entrySet()) {
      com.google.protobuf.MapEntry<String, String>
      metadata__ = MetadataDefaultEntryHolder.defaultEntry.newBuilderForType()
          .setKey(entry.getKey())
          .setValue(entry.getValue())
          .build();
      size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(6, metadata__);
    }
    if (body_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(7, getBody());
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof Message)) {
      return super.equals(obj);
    }
    Message other = (Message) obj;

    if (getId()
        != other.getId()) return false;
    if (!getEncoding()
        .equals(other.getEncoding())) return false;
    if (!getCodec()
        .equals(other.getCodec())) return false;
    if (!getCompress()
        .equals(other.getCompress())) return false;
    if (!getMessageType()
        .equals(other.getMessageType())) return false;
    if (!internalGetMetadata().equals(
        other.internalGetMetadata())) return false;
    if (hasBody() != other.hasBody()) return false;
    if (hasBody()) {
      if (!getBody()
          .equals(other.getBody())) return false;
    }
    if (!unknownFields.equals(other.unknownFields)) return false;
    return true;
  }

  @Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + ID_FIELD_NUMBER;
    hash = (53 * hash) + getId();
    hash = (37 * hash) + ENCODING_FIELD_NUMBER;
    hash = (53 * hash) + getEncoding().hashCode();
    hash = (37 * hash) + CODEC_FIELD_NUMBER;
    hash = (53 * hash) + getCodec().hashCode();
    hash = (37 * hash) + COMPRESS_FIELD_NUMBER;
    hash = (53 * hash) + getCompress().hashCode();
    hash = (37 * hash) + MESSAGETYPE_FIELD_NUMBER;
    hash = (53 * hash) + getMessageType().hashCode();
    if (!internalGetMetadata().getMap().isEmpty()) {
      hash = (37 * hash) + METADATA_FIELD_NUMBER;
      hash = (53 * hash) + internalGetMetadata().hashCode();
    }
    if (hasBody()) {
      hash = (37 * hash) + BODY_FIELD_NUMBER;
      hash = (53 * hash) + getBody().hashCode();
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static Message parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static Message parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static Message parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static Message parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static Message parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static Message parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static Message parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static Message parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static Message parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static Message parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static Message parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static Message parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  @Override
  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(Message prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  @Override
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @Override
  protected Builder newBuilderForType(
      BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code hotrefresh.v1.Message}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:hotrefresh.v1.Message)
      MessageOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return RemotingApiProto.internal_static_hotrefresh_v1_Message_descriptor;
    }

    @SuppressWarnings({"rawtypes"})
    protected com.google.protobuf.MapField internalGetMapField(
        int number) {
      switch (number) {
        case 6:
          return internalGetMetadata();
        default:
          throw new RuntimeException(
              "Invalid map field number: " + number);
      }
    }
    @SuppressWarnings({"rawtypes"})
    protected com.google.protobuf.MapField internalGetMutableMapField(
        int number) {
      switch (number) {
        case 6:
          return internalGetMutableMetadata();
        default:
          throw new RuntimeException(
              "Invalid map field number: " + number);
      }
    }
    @Override
    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return RemotingApiProto.internal_static_hotrefresh_v1_Message_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              Message.class, Builder.class);
    }

    // Construct using com.hyf.hotrefresh.plugin.grpc.generate.Message.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    @Override
    public Builder clear() {
      super.clear();
      id_ = 0;

      encoding_ = com.google.protobuf.ByteString.EMPTY;

      codec_ = com.google.protobuf.ByteString.EMPTY;

      compress_ = com.google.protobuf.ByteString.EMPTY;

      messageType_ = com.google.protobuf.ByteString.EMPTY;

      internalGetMutableMetadata().clear();
      if (bodyBuilder_ == null) {
        body_ = null;
      } else {
        body_ = null;
        bodyBuilder_ = null;
      }
      return this;
    }

    @Override
    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return RemotingApiProto.internal_static_hotrefresh_v1_Message_descriptor;
    }

    @Override
    public Message getDefaultInstanceForType() {
      return Message.getDefaultInstance();
    }

    @Override
    public Message build() {
      Message result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    @Override
    public Message buildPartial() {
      Message result = new Message(this);
      int from_bitField0_ = bitField0_;
      result.id_ = id_;
      result.encoding_ = encoding_;
      result.codec_ = codec_;
      result.compress_ = compress_;
      result.messageType_ = messageType_;
      result.metadata_ = internalGetMetadata();
      result.metadata_.makeImmutable();
      if (bodyBuilder_ == null) {
        result.body_ = body_;
      } else {
        result.body_ = bodyBuilder_.build();
      }
      onBuilt();
      return result;
    }

    @Override
    public Builder clone() {
      return super.clone();
    }
    @Override
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.setField(field, value);
    }
    @Override
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    @Override
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    @Override
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return super.setRepeatedField(field, index, value);
    }
    @Override
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return super.addRepeatedField(field, value);
    }
    @Override
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof Message) {
        return mergeFrom((Message)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(Message other) {
      if (other == Message.getDefaultInstance()) return this;
      if (other.getId() != 0) {
        setId(other.getId());
      }
      if (other.getEncoding() != com.google.protobuf.ByteString.EMPTY) {
        setEncoding(other.getEncoding());
      }
      if (other.getCodec() != com.google.protobuf.ByteString.EMPTY) {
        setCodec(other.getCodec());
      }
      if (other.getCompress() != com.google.protobuf.ByteString.EMPTY) {
        setCompress(other.getCompress());
      }
      if (other.getMessageType() != com.google.protobuf.ByteString.EMPTY) {
        setMessageType(other.getMessageType());
      }
      internalGetMutableMetadata().mergeFrom(
          other.internalGetMetadata());
      if (other.hasBody()) {
        mergeBody(other.getBody());
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    @Override
    public final boolean isInitialized() {
      return true;
    }

    @Override
    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      Message parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (Message) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int bitField0_;

    private int id_ ;
    /**
     * <code>int32 id = 1;</code>
     * @return The id.
     */
    @Override
    public int getId() {
      return id_;
    }
    /**
     * <code>int32 id = 1;</code>
     * @param value The id to set.
     * @return This builder for chaining.
     */
    public Builder setId(int value) {
      
      id_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>int32 id = 1;</code>
     * @return This builder for chaining.
     */
    public Builder clearId() {
      
      id_ = 0;
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString encoding_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes encoding = 2;</code>
     * @return The encoding.
     */
    @Override
    public com.google.protobuf.ByteString getEncoding() {
      return encoding_;
    }
    /**
     * <code>bytes encoding = 2;</code>
     * @param value The encoding to set.
     * @return This builder for chaining.
     */
    public Builder setEncoding(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      encoding_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bytes encoding = 2;</code>
     * @return This builder for chaining.
     */
    public Builder clearEncoding() {
      
      encoding_ = getDefaultInstance().getEncoding();
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString codec_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes codec = 3;</code>
     * @return The codec.
     */
    @Override
    public com.google.protobuf.ByteString getCodec() {
      return codec_;
    }
    /**
     * <code>bytes codec = 3;</code>
     * @param value The codec to set.
     * @return This builder for chaining.
     */
    public Builder setCodec(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      codec_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bytes codec = 3;</code>
     * @return This builder for chaining.
     */
    public Builder clearCodec() {
      
      codec_ = getDefaultInstance().getCodec();
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString compress_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes compress = 4;</code>
     * @return The compress.
     */
    @Override
    public com.google.protobuf.ByteString getCompress() {
      return compress_;
    }
    /**
     * <code>bytes compress = 4;</code>
     * @param value The compress to set.
     * @return This builder for chaining.
     */
    public Builder setCompress(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      compress_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bytes compress = 4;</code>
     * @return This builder for chaining.
     */
    public Builder clearCompress() {
      
      compress_ = getDefaultInstance().getCompress();
      onChanged();
      return this;
    }

    private com.google.protobuf.ByteString messageType_ = com.google.protobuf.ByteString.EMPTY;
    /**
     * <code>bytes messageType = 5;</code>
     * @return The messageType.
     */
    @Override
    public com.google.protobuf.ByteString getMessageType() {
      return messageType_;
    }
    /**
     * <code>bytes messageType = 5;</code>
     * @param value The messageType to set.
     * @return This builder for chaining.
     */
    public Builder setMessageType(com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      messageType_ = value;
      onChanged();
      return this;
    }
    /**
     * <code>bytes messageType = 5;</code>
     * @return This builder for chaining.
     */
    public Builder clearMessageType() {
      
      messageType_ = getDefaultInstance().getMessageType();
      onChanged();
      return this;
    }

    private com.google.protobuf.MapField<
        String, String> metadata_;
    private com.google.protobuf.MapField<String, String>
    internalGetMetadata() {
      if (metadata_ == null) {
        return com.google.protobuf.MapField.emptyMapField(
            MetadataDefaultEntryHolder.defaultEntry);
      }
      return metadata_;
    }
    private com.google.protobuf.MapField<String, String>
    internalGetMutableMetadata() {
      onChanged();;
      if (metadata_ == null) {
        metadata_ = com.google.protobuf.MapField.newMapField(
            MetadataDefaultEntryHolder.defaultEntry);
      }
      if (!metadata_.isMutable()) {
        metadata_ = metadata_.copy();
      }
      return metadata_;
    }

    public int getMetadataCount() {
      return internalGetMetadata().getMap().size();
    }
    /**
     * <code>map&lt;string, string&gt; metadata = 6;</code>
     */

    @Override
    public boolean containsMetadata(
        String key) {
      if (key == null) { throw new NullPointerException(); }
      return internalGetMetadata().getMap().containsKey(key);
    }
    /**
     * Use {@link #getMetadataMap()} instead.
     */
    @Override
    @Deprecated
    public java.util.Map<String, String> getMetadata() {
      return getMetadataMap();
    }
    /**
     * <code>map&lt;string, string&gt; metadata = 6;</code>
     */
    @Override

    public java.util.Map<String, String> getMetadataMap() {
      return internalGetMetadata().getMap();
    }
    /**
     * <code>map&lt;string, string&gt; metadata = 6;</code>
     */
    @Override

    public String getMetadataOrDefault(
        String key,
        String defaultValue) {
      if (key == null) { throw new NullPointerException(); }
      java.util.Map<String, String> map =
          internalGetMetadata().getMap();
      return map.containsKey(key) ? map.get(key) : defaultValue;
    }
    /**
     * <code>map&lt;string, string&gt; metadata = 6;</code>
     */
    @Override

    public String getMetadataOrThrow(
        String key) {
      if (key == null) { throw new NullPointerException(); }
      java.util.Map<String, String> map =
          internalGetMetadata().getMap();
      if (!map.containsKey(key)) {
        throw new IllegalArgumentException();
      }
      return map.get(key);
    }

    public Builder clearMetadata() {
      internalGetMutableMetadata().getMutableMap()
          .clear();
      return this;
    }
    /**
     * <code>map&lt;string, string&gt; metadata = 6;</code>
     */

    public Builder removeMetadata(
        String key) {
      if (key == null) { throw new NullPointerException(); }
      internalGetMutableMetadata().getMutableMap()
          .remove(key);
      return this;
    }
    /**
     * Use alternate mutation accessors instead.
     */
    @Deprecated
    public java.util.Map<String, String>
    getMutableMetadata() {
      return internalGetMutableMetadata().getMutableMap();
    }
    /**
     * <code>map&lt;string, string&gt; metadata = 6;</code>
     */
    public Builder putMetadata(
        String key,
        String value) {
      if (key == null) { throw new NullPointerException(); }
      if (value == null) { throw new NullPointerException(); }
      internalGetMutableMetadata().getMutableMap()
          .put(key, value);
      return this;
    }
    /**
     * <code>map&lt;string, string&gt; metadata = 6;</code>
     */

    public Builder putAllMetadata(
        java.util.Map<String, String> values) {
      internalGetMutableMetadata().getMutableMap()
          .putAll(values);
      return this;
    }

    private com.google.protobuf.Any body_;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder> bodyBuilder_;
    /**
     * <code>.google.protobuf.Any body = 7;</code>
     * @return Whether the body field is set.
     */
    public boolean hasBody() {
      return bodyBuilder_ != null || body_ != null;
    }
    /**
     * <code>.google.protobuf.Any body = 7;</code>
     * @return The body.
     */
    public com.google.protobuf.Any getBody() {
      if (bodyBuilder_ == null) {
        return body_ == null ? com.google.protobuf.Any.getDefaultInstance() : body_;
      } else {
        return bodyBuilder_.getMessage();
      }
    }
    /**
     * <code>.google.protobuf.Any body = 7;</code>
     */
    public Builder setBody(com.google.protobuf.Any value) {
      if (bodyBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        body_ = value;
        onChanged();
      } else {
        bodyBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <code>.google.protobuf.Any body = 7;</code>
     */
    public Builder setBody(
        com.google.protobuf.Any.Builder builderForValue) {
      if (bodyBuilder_ == null) {
        body_ = builderForValue.build();
        onChanged();
      } else {
        bodyBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <code>.google.protobuf.Any body = 7;</code>
     */
    public Builder mergeBody(com.google.protobuf.Any value) {
      if (bodyBuilder_ == null) {
        if (body_ != null) {
          body_ =
            com.google.protobuf.Any.newBuilder(body_).mergeFrom(value).buildPartial();
        } else {
          body_ = value;
        }
        onChanged();
      } else {
        bodyBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <code>.google.protobuf.Any body = 7;</code>
     */
    public Builder clearBody() {
      if (bodyBuilder_ == null) {
        body_ = null;
        onChanged();
      } else {
        body_ = null;
        bodyBuilder_ = null;
      }

      return this;
    }
    /**
     * <code>.google.protobuf.Any body = 7;</code>
     */
    public com.google.protobuf.Any.Builder getBodyBuilder() {
      
      onChanged();
      return getBodyFieldBuilder().getBuilder();
    }
    /**
     * <code>.google.protobuf.Any body = 7;</code>
     */
    public com.google.protobuf.AnyOrBuilder getBodyOrBuilder() {
      if (bodyBuilder_ != null) {
        return bodyBuilder_.getMessageOrBuilder();
      } else {
        return body_ == null ?
            com.google.protobuf.Any.getDefaultInstance() : body_;
      }
    }
    /**
     * <code>.google.protobuf.Any body = 7;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder> 
        getBodyFieldBuilder() {
      if (bodyBuilder_ == null) {
        bodyBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder>(
                getBody(),
                getParentForChildren(),
                isClean());
        body_ = null;
      }
      return bodyBuilder_;
    }
    @Override
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }

    @Override
    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:hotrefresh.v1.Message)
  }

  // @@protoc_insertion_point(class_scope:hotrefresh.v1.Message)
  private static final Message DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new Message();
  }

  public static Message getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Message>
      PARSER = new com.google.protobuf.AbstractParser<Message>() {
    @Override
    public Message parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return new Message(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<Message> parser() {
    return PARSER;
  }

  @Override
  public com.google.protobuf.Parser<Message> getParserForType() {
    return PARSER;
  }

  @Override
  public Message getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

