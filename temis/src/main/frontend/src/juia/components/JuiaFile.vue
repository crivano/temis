<template>
  <div>
    <div class="vfa-demo bg-light">
      <VueFileAgent
        class="upload-block"
        ref="vueFileAgent"
        :theme="'list'"
        :multiple="false"
        :deletable="true"
        :meta="true"
        :accept="'application/pdf'"
        :maxSize="'10MB'"
        :helpText="'Selecione o arquivo para upload'"
        :errorText="{
          type: 'Tipo de arquivo inválido',
          size: 'Arquivo não pode ser maior que 10MB',
        }"
        @select="filesSelected($event)"
        @beforedelete="onBeforeDelete($event)"
        @delete="fileDeleted($event)"
        @upload="fileUploaded($event)"
        v-model="fileRecords"
      >
        <template v-slot:before-outer></template>
        <template v-slot:file-preview="slotProps">
          <div :key="slotProps.index" class="grid-box-item file-row">
            <button
              type="button"
              class="close remove m-1"
              aria-label="Remove"
              @click="removeFileRecord(slotProps.fileRecord)"
            >
              <span aria-hidden="true">&times;</span>
            </button>
            <div
              class="progress"
              :class="{ completed: slotProps.fileRecord.progress() == 100 }"
            >
              <div
                class="progress-bar"
                role="progressbar"
                :style="{ width: slotProps.fileRecord.progress() + '%' }"
              ></div>
            </div>
            <strong>{{ slotProps.fileRecord.name() }}</strong>
            <span class="text-muted">({{ slotProps.fileRecord.size() }})</span>
          </div>
        </template>
        <template v-slot:file-preview-new>
          <div class="text-center my-1" key="new">
            <a href="#" class="">Selecione o arquivo</a> ou arraste e solte
            aqui.
          </div>
        </template>
      </VueFileAgent>
    </div>
    <validation-provider
      :rules="validate"
      :immediate="immediate"
      v-slot="{ validate: validateMethod, errors }"
      ref="vp"
    >
      <input
        type="hidden"
        class="form-control xmyinput"
        :class="{ 'is-invalid': errors.length > 0 }"
        v-bind:value="value"
      />
      <div
        class="invalid-feedback"
        v-if="errors[0] != 'XPreenchimento obrigatório'"
      >
        {{ errors[0] }}
      </div>
    </validation-provider>
  </div>
</template>

<script>
// import { mask } from "vue-the-mask";
export default {
  name: "juia-file",
  props: {
    immediate: { type: Boolean, default: true },
    value: Object,
    name: String,
    error: String,
    validate: String,
  },
  data: function() {
    return {
      fileRecords: [],
      idArquivo: undefined,
      uploadUrl: "http://localhost:8080/temis/app/upload",
      uploadHeaders: { "X-Test-Header": "vue-file-agent" },
      fileRecordsForUpload: [],
    };
  },
  methods: {
    uploadFiles: function() {
      console.log("vou fazer o upload");
      // Using the default uploader. You may use another uploader instead.
      this.$refs.vueFileAgent.upload(
        this.uploadUrl,
        this.uploadHeaders,
        this.fileRecordsForUpload
      );
      this.fileRecordsForUpload = [];
    },
    deleteUploadedFile: function(fileRecord) {
      // Using the default uploader. You may use another uploader instead.
      this.$refs.vueFileAgent.deleteUpload(
        this.uploadUrl,
        this.uploadHeaders,
        fileRecord
      );
    },
    filesSelected: function(fileRecordsNewlySelected) {
      var validFileRecords = fileRecordsNewlySelected.filter(
        (fileRecord) => !fileRecord.error
      );
      this.fileRecordsForUpload = this.fileRecordsForUpload.concat(
        validFileRecords
      );
      if (this.fileRecordsForUpload.length > 0) this.uploadFiles();
    },
    onBeforeDelete: function(fileRecord) {
      var i = this.fileRecordsForUpload.indexOf(fileRecord);
      if (i !== -1) {
        this.fileRecordsForUpload.splice(i, 1);
      } else {
        if (confirm("Are you sure you want to delete?")) {
          this.$refs.vueFileAgent.deleteFileRecord(fileRecord); // will trigger 'delete' event
        }
      }
    },
    fileDeleted: function(fileRecord) {
      var i = this.fileRecordsForUpload.indexOf(fileRecord);
      if (i !== -1) {
        this.fileRecordsForUpload.splice(i, 1);
      } else {
        this.deleteUploadedFile(fileRecord);
      }
    },
    fileUploaded: function() {
      console.log("fileUploaded");
      this.$emit("input", this.fileRecords[0].upload.data);
    },
  },
};
</script>
<style scoped>
.cfg {
  margin-bottom: 0.5em;
}
.myinput {
  background-image: none !important;
}

.vue-file-agent .file-preview-wrapper:before {
  background: rgba(0, 0, 0, 0) !important;
}
</style>
