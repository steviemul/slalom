package io.steviemul.slalom.store.exception;

public class StoreException extends Exception {

  public StoreException(String message, Exception root) {
    super(message, root);
  }
}
