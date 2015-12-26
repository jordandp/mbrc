package com.kelsos.mbrc.dto.library;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@JsonInclude(JsonInclude.Include.NON_NULL)

@JsonPropertyOrder({
    "name",
    "artist_id",
    "cover_id",
    "album_id",
    "id",
    "date_added",
    "date_updated",
    "date_deleted"
}) public class AlbumDto {

  @JsonProperty("name") private String name;
  @JsonProperty("artist_id") private int artistId;
  @JsonProperty("cover_id") private int coverId;
  @JsonProperty("album_id") private String albumId;
  @JsonProperty("id") private int id;
  @JsonProperty("date_added") private long dateAdded;
  @JsonProperty("date_updated") private long dateUpdated;
  @JsonProperty("date_deleted") private long dateDeleted;

  /**
   * @return The name
   */
  @JsonProperty("name") public String getName() {
    return name;
  }

  /**
   * @param name The name
   */
  @JsonProperty("name") public void setName(String name) {
    this.name = name;
  }

  /**
   * @return The artistId
   */
  @JsonProperty("artist_id") public int getArtistId() {
    return artistId;
  }

  /**
   * @param artistId The artist_id
   */
  @JsonProperty("artist_id") public void setArtistId(int artistId) {
    this.artistId = artistId;
  }

  /**
   * @return The coverId
   */
  @JsonProperty("cover_id") public int getCoverId() {
    return coverId;
  }

  /**
   * @param coverId The cover_id
   */
  @JsonProperty("cover_id") public void setCoverId(int coverId) {
    this.coverId = coverId;
  }

  /**
   * @return The albumId
   */
  @JsonProperty("album_id") public String getAlbumId() {
    return albumId;
  }

  /**
   * @param albumId The album_id
   */
  @JsonProperty("album_id") public void setAlbumId(String albumId) {
    this.albumId = albumId;
  }

  /**
   * @return The id
   */
  @JsonProperty("id") public int getId() {
    return id;
  }

  /**
   * @param id The id
   */
  @JsonProperty("id") public void setId(int id) {
    this.id = id;
  }

  /**
   * @return The dateAdded
   */
  @JsonProperty("date_added") public long getDateAdded() {
    return dateAdded;
  }

  /**
   * @param dateAdded The date_added
   */
  @JsonProperty("date_added") public void setDateAdded(long dateAdded) {
    this.dateAdded = dateAdded;
  }

  /**
   * @return The dateUpdated
   */
  @JsonProperty("date_updated") public long getDateUpdated() {
    return dateUpdated;
  }

  /**
   * @param dateUpdated The date_updated
   */
  @JsonProperty("date_updated") public void setDateUpdated(long dateUpdated) {
    this.dateUpdated = dateUpdated;
  }

  /**
   * @return The dateDeleted
   */
  @JsonProperty("date_deleted") public long getDateDeleted() {
    return dateDeleted;
  }

  /**
   * @param dateDeleted The date_deleted
   */
  @JsonProperty("date_deleted") public void setDateDeleted(long dateDeleted) {
    this.dateDeleted = dateDeleted;
  }

  @Override public String toString() {
    return ToStringBuilder.reflectionToString(this);
  }

  @Override public int hashCode() {
    return new HashCodeBuilder().append(name)
        .append(artistId)
        .append(coverId)
        .append(albumId)
        .append(id)
        .append(dateAdded)
        .append(dateUpdated)
        .append(dateDeleted)
        .toHashCode();
  }

  @Override public boolean equals(Object other) {
    if (other == this) {
      return true;
    }
    if (!(other instanceof AlbumDto)) {
      return false;
    }
    AlbumDto rhs = ((AlbumDto) other);
    return new EqualsBuilder().append(name, rhs.name)
        .append(artistId, rhs.artistId)
        .append(coverId, rhs.coverId)
        .append(albumId, rhs.albumId)
        .append(id, rhs.id)
        .append(dateAdded, rhs.dateAdded)
        .append(dateUpdated, rhs.dateUpdated)
        .append(dateDeleted, rhs.dateDeleted)
        .isEquals();
  }
}